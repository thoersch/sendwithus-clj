(defproject sendwithus-clj "1.0.0-SNAPSHOT"
  :description "Clojure SDK for SendWithUs API"
  :url "https://github.com/thoersch/sendwithus-clj"
  :scm {:name "git"
        :url "https://github.com/thoersch/sendwithus-clj"}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.1.2"]
                 [clj-http "0.3.5"]]
  :signing {:gpg-key "375B1908"}
  :pom-addition [:developers [:developer
                              [:name "Tyler Hoersch"]
                              [:url "http://tylerhoersch.com"]
                              [:email "thoersch@gmail.com"]
                              [:timezone "-5"]]]
  :plugins [[lein-marginalia "0.8.0"]])
