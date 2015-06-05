(defproject sendwithus-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.1.2"]
                 [clj-http "0.3.5"]]
  :main ^:skip-aot sendwithus-clj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
