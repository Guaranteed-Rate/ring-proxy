(ns tailrecursion.ring-proxy-test
  (:require [clojure.test :refer :all])
  (:require [tailrecursion.ring-proxy :refer [wrap-proxy]]))


(defn scenario [proxy-endpoint proxy-remote request-endpoint]
  (let [inspection (atom nil)
        handler (wrap-proxy (fn [req]) proxy-endpoint proxy-remote)]
    (with-redefs [clj-http.client/request (fn [req] (reset! inspection req))]
      (handler {:uri request-endpoint})
      (:url @inspection))))


(deftest protect-against-directory-traversals
  (are [proxy-endpoint proxy-remote request-endpoint expected-remote]
    (= expected-remote (scenario proxy-endpoint proxy-remote request-endpoint))

    "/noggly" "https://nogs.noggle.com"
    "/noggly//google.com" "https://nogs.noggle.com//google.com"

    "/noggly" "https://nogs.noggle.com"
    "/noggly//../google.com" "https://nogs.noggle.com//../google.com"

    "/noggly" "https://nogs.noggle.com"
    "/noggly/" "https://nogs.noggle.com/"

    "/" "https://nogs.noggle.com"
    "/noggly" "https://nogs.noggle.com/noggly"

    "/" "https://nogs.noggle.com"
    "/" "https://nogs.noggle.com"))
