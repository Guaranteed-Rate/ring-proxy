(ns tailrecursion.ring-proxy
  (:require
    [clj-http.client :refer [request]]
    [clojure.string :refer [join split ends-with? starts-with? blank?]]
    [ring.adapter.jetty :refer [run-jetty]]
    [ring.middleware.cookies :refer [wrap-cookies]]))

(defn prepare-cookies
  "Removes the :domain and :secure keys and converts the :expires key (a Date)
  to a string in the ring response map resp. Returns resp with cookies properly
  munged."
  [resp]
  (let [prepare #(-> (update-in % [1 :expires] str)
                     (update-in [1] dissoc :domain :secure))]
    (assoc resp :cookies (into {} (map prepare (:cookies resp))))))

(defn slurp-binary
  "Reads len bytes from InputStream is and returns a byte array."
  [^java.io.InputStream is len]
  (with-open [rdr is]
    (let [buf (byte-array len)]
      (.read rdr buf)
      buf)))

(defn join-paths
  "Safely combines a uri and a remote path."
  [local-path base-remote accessed-path]
  (let [remainder (subs accessed-path (.length local-path))]
    (if (blank? remainder)
      base-remote
      (case [(ends-with? base-remote "/") (starts-with? remainder "/")]
        [false false]
        (str base-remote "/" remainder)
        ([false true] [true false])
        (str base-remote remainder)
        [true true]
        (str (subs base-remote 0 (dec (.length base-remote))) remainder)))))

(defn wrap-proxy
  "Proxies requests from proxied-path, a local URI, to the remote URI at
  remote-base-uri, also a string."
  [handler ^String proxied-path remote-base-uri & [http-opts]]
  (wrap-cookies
    (fn [req]
      (if (.startsWith ^String (:uri req) proxied-path)
        (let [remote-uri (join-paths proxied-path remote-base-uri (:uri req))]
          (-> (merge {:method           (:request-method req)
                      :url              (if (blank? (:query-string req))
                                          remote-uri
                                          (str remote-uri "?" (:query-string req)))
                      :headers          (dissoc (:headers req) "host" "content-length")
                      :body             (if-let [len (get-in req [:headers "content-length"])]
                                          (slurp-binary (:body req) (Integer/parseInt len)))
                      :follow-redirects true
                      :throw-exceptions false
                      :as               :stream} http-opts)
              request
              prepare-cookies))
        (handler req)))))

(defn run-proxy
  [listen-path listen-port remote-uri http-opts]
  (-> (constantly {:status 404 :headers {} :body "404 - not found"})
      (wrap-proxy listen-path remote-uri http-opts)
      (run-jetty {:port listen-port}) ))
