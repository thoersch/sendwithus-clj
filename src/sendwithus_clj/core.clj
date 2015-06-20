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
(defrecord Recipient [address name])
(defrecord Sender [address reply_to name])
(defrecord Email [email_id recipient cc bcc sender email_data tags headers inline files esp_account locale version_name])
(defrecord RenderRequest [template_id template_data version_id version_name locale strict])

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

(defn- do-delete [request]
  (read-json
    (:body
      (http/delete (make-uri request) {:query-params (parameter-string (:query request)) :headers (make-headers)}))))

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

(defn add-version [template]
  (let [template-id (:id template)
        locale (:locale template)]
  (if (empty? locale)
    (do-post (Request. (str "templates/" template-id "/versions") nil template))
    (do-post (Request. (str "templates/" template-id "/locales/" locale "/versions") nil template)))))

(defn get-logs
  ([] (do-get (Request. "logs" nil nil)))
  ([log-id events?]
    (if (false? events?)
      (do-get (Request. (str "logs/" log-id) nil nil))
      (do-get (Request. (str "logs/" log-id "/events") nil nil)))))

(defn resend-email [log-id]
  (do-post (Request. "resend" nil {:log_id log-id})))

(defn send-email [email]
  (do-post (Request. "send" nil email)))

(defn get-snippets
  ([] (do-get (Request. "snippets" nil nil)))
  ([snippet-id] (do-get (Request. (str "snippets/" snippet-id) nil nil))))

(defn create-snippet [name body]
  (do-post (Request. "snippets" nil {:name name :body body})))

(defn update-snippet [snippet-id name body]
  (do-put (Request. (str "snippets/" snippet-id) nil {:name name :body body})))

(defn delete-snippet [snippet-id]
  (do-delete (Request. (str "snippets/" snippet-id) nil nil)))

(defn render-email [render-options]
  (do-post (Request. "render" nil render-options)))

(defn get-providers []
  (do-get (Request. "esp_accounts" nil nil)))

(defn -main [& args]
  (with-send-with-us "live_4c90f69f882402aa9847d520c86ecd4f72b8a030"
    (println
      (get-providers))))


(comment (send-email (Email.
                    "tem_B76FSNaAtKYYeqnALoBYVh"
                    (Recipient. "thoersch@gmail.com" "Tyler Hoersch")
                    [(Recipient. "cc-test1@test.com" "cc test")]
                    [(Recipient. "bcc-test1@test.com" "bcc test")]
                    (Sender. "swu-clj-client@gmail.com" "noreply@test.com" "SWU clj")
                    {:amount "$12.99"}
                    ["tag1" "tag2"]
                    {:X-HEADER-ONE "custom header"}
                    {:id "inline-message" :data "SGkgdGhpcyBpcyBhIG1lc3NhZ2U="}
                    [{:id "doc.txt" :data "SGVsbG8sIHRoaXMgaXMgYSB0ZXh0IGZpbGUuCg=="}]
                    nil
                    "en-US"
                    "Version"))
)
      ;(add-version (Template. "tem_B76FSNaAtKYYeqnALoBYVh" "ru-RU" nil "Version" "My Spanish Subject" "<html><head><title></title></head><body>hola!</body></html>" "hola")))))
