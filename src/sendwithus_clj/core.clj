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

(defrecord Request [path query body])
(defrecord Template [id locale version name subject html text])

(defmacro with-send-with-us [key & body]
  `(binding [*swu-key* ~key]
     ~@body))

(defn- make-uri [request]
  (str host-base (:path request)))

(defn- parameter-string [params]
  (join "&"
        (map (fn [[key val]] (str (name key) "=" (str val)))
             (sort-by #(name (key %)) java.lang.String/CASE_INSENSITIVE_ORDER params))))

(defn- make-headers []
  {"Accept" "text/plain"
   "Content-Type" "application/json;charset=UTF-8"
   swu-api-header *swu-key*
   swu-client-header user-agent})

(defn- do-get [request]
  (read-json (:body (http/get (make-uri request)
              {:body (:body request)
               :query-params (:query request)
               :headers (make-headers)}))))

(defn- do-post [request]
  (read-json
    (:body
      (http/post
        (make-uri request)
        {:body (json-str (:body request))
         :query-params (parameter-string (:query request))
         :headers (make-headers)}))))

(defn- do-put [request]
  (read-json
    (:body
      (http/put
        (make-uri request)
        {:body (json-str (:body request))
         :query-params (parameter-string (:query request))
         :headers (make-headers)}))))

(defn get-templates
  ([] (do-get (Request. "templates" nil nil)))
  ([template-id] (do-get (Request. (str "templates/" template-id) nil nil)))
  ([template-id locale] (do-get (Request. (str "templates/" template-id "/locales/" locale) nil nil)))
  ([template-id locale version] (do-get (Request. (str "templates/" template-id "/locales/" locale "/versions/" version) nil nil))))

(defn update-template [template]
  (let [template-id (:id template)
        locale (:locale template)
        version (:version template)]
    (do-put (Request. (str "templates/" template-id "/locales/" locale "/versions/" version) nil template))))

(defn create-template [template]
  (do-post (Request. "templates" nil template)))

(defn add-locale [template]
  (let [template-id (:id template)]
    (do-post (Request. (str "templates/" template-id "/locales") nil template))))

(defn -main [& args]
  (with-send-with-us "live_4c90f69f882402aa9847d520c86ecd4f72b8a030"
    (println
      (add-locale (Template. "tem_B76FSNaAtKYYeqnALoBYVh" "es-US" nil "New Spanish Template" "My Spanish Subject" "<html><head><title></title></head><body>hola!</body></html>" "hola")))))
