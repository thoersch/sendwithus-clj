(ns sendwithus-clj.core
  (:require
   [clj-http.client :as http])
  (:use [clojure.data.json :only [json-str read-json]]
        [clojure.string :only [join]]))

(def client-version 0.1)
(def user-agent (format "clojure-%f" client-version))
(def host-base "https://api.sendwithus.com/api/v1/")
(def swu-api-header "X-SWU-API-KEY")
(def swu-client-header "X-SWU-API-CLIENT")
(def ^{:dynamic true} *swu-key* nil)

(defstruct request :method :path :query :body)

(defmacro with-send-with-us [key & body]
  `(binding [*swu-key* ~key]
     ~@body))

(defn- make-uri [request]
  (str host-base (request :path)))

(defn- parameter-string [params]
  (join "&"
        (map (fn [[key val]] (str (name key) "=" (str val)))
             (sort-by #(name (key %)) java.lang.String/CASE_INSENSITIVE_ORDER params))))

(defn- make-headers []
  {"Accept" "text/plain"
   "Content-Type" "application/json;charset=UTF-8"
   swu-api-header *swu-key*
   swu-client-header user-agent})

(defn- do-get! [request]
  (read-json (:body (http/get (make-uri request)
              {:body (request :body)
               :query-params (:query request)
               :headers (make-headers)}))))

(defn- do-post! [request]
  (read-json
    (:body
      (http/post
        (make-uri request)
        {:body
          (parameter-string
           (merge
              (:query request) (assoc {} :data (json-str (request :body)))))
         :query-params nil
         :headers (make-headers)}))))

(defn templates []
  (do-get! "templates"))

(defn -main [& args]
  (with-send-with-us "some key"
    (let [request (struct request "GET" "templates" nil nil)]
      (println (do-get! request)))))
