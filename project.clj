(defproject com.guaranteedrate/ring-proxy "3.0.0"
  :description "HTTP proxy ring middleware for Clojure web applications."
  :url "https://github.com/Guaranteed-Rate/ring-proxy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths ["src"]
  :min-lein-version "2.3.4"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "2.0.0"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-jetty-adapter "1.3.2"]])
