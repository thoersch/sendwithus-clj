(ns sendwithus-clj.core
  (:require
   [clj-http.client :as http])
  (:use [clojure.data.json :only [json-str read-json]]
        [clojure.string :only [join]]))

(def client-version 0.1)
(def user-agent (format "clojure-%f" client-version))
(def api-base "/api/v1/")
(def host-base (str "https://api.sendwithus.com" api-base))
(def swu-api-header "X-SWU-API-KEY")
(def swu-client-header "X-SWU-API-CLIENT")
(def ^{:dynamic true} *swu-key* nil)
(def batch-path (str api-base "send"))
(def batch-method "POST")

(defrecord Request [path query body])
(defrecord Template [id locale version name subject html text])
(defrecord Recipient [address name])
(defrecord Sender [address reply_to name])
(defrecord Email [email_id recipient cc bcc sender email_data tags headers inline files esp_account locale version_name])
(defrecord RenderRequest [template_id template_data version_id version_name locale strict])
(defrecord DripCampaign [drip_campaign_id recipient cc bcc sender email_data tags esp_account locale])

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
  "Gets all - no args
   Gets one - [template-id]
   Gets one with a specific locale - [template-id locale]
   Gets one with a specific locale and version - [template-id locale version]"
  ([] (do-get (Request. "templates" nil nil)))
  ([template-id] (do-get (Request. (str "templates/" template-id) nil nil)))
  ([template-id locale] (do-get (Request. (str "templates/" template-id "/locales/" locale) nil nil)))
  ([template-id locale version] (do-get (Request. (str "templates/" template-id "/locales/" locale "/versions/" version) nil nil))))

(defn update-template [template]
  "Updates an existing template - [Template.]"
  (let [template-id (:id template)
        locale (:locale template)
        version (:version template)]
    (do-put (Request. (str "templates/" template-id "/locales/" locale "/versions/" version) nil template))))

(defn create-template [template]
  "Creates a new template - [Template.]"
  (do-post (Request. "templates" nil template)))

(defn add-locale [template]
  "Adds locale to existing template - [Template.]"
  (let [template-id (:id template)]
    (do-post (Request. (str "templates/" template-id "/locales") nil template))))

(defn add-version [template]
  "Adds a version to template - [Template.] (without locale)
   Adds a version to a specific locale - [Template.] (with locale)"
  (let [template-id (:id template)
        locale (:locale template)]
  (if (empty? locale)
    (do-post (Request. (str "templates/" template-id "/versions") nil template))
    (do-post (Request. (str "templates/" template-id "/locales/" locale "/versions") nil template)))))

(defn get-logs
  "Gets all logs - no args
   Gets specific logs - [log-id show-events(optional)]"
  ([] (do-get (Request. "logs" nil nil)))
  ([log-id events?]
    (if (false? events?)
      (do-get (Request. (str "logs/" log-id) nil nil))
      (do-get (Request. (str "logs/" log-id "/events") nil nil)))))

(defn resend-email [log-id]
  "Resends a specific email based on log id - [log-id]"
  (do-post (Request. "resend" nil {:log_id log-id})))

(defn send-email [email]
  "Sends an email - [Email.]"
  (do-post (Request. "send" nil email)))

(defn get-snippets
  "Gets all snippets - no args
   Gets a specific snippet - [snippet-id]"
  ([] (do-get (Request. "snippets" nil nil)))
  ([snippet-id] (do-get (Request. (str "snippets/" snippet-id) nil nil))))

(defn create-snippet [name body]
  "Creates a new snippet - [name body]"
  (do-post (Request. "snippets" nil {:name name :body body})))

(defn update-snippet [snippet-id name body]
  "Updates an existing snippet - [snippet-id name body]"
  (do-put (Request. (str "snippets/" snippet-id) nil {:name name :body body})))

(defn delete-snippet [snippet-id]
  "Deletes an existing snippet - [snippet-id]"
  (do-delete (Request. (str "snippets/" snippet-id) nil nil)))

(defn render-email [render-options]
  "Renders an email based on options - [RenderRequest.]"
  (do-post (Request. "render" nil render-options)))

(defn get-providers []
  "Gets all email service providers - no args"
  (do-get (Request. "esp_accounts" nil nil)))

(defn set-default-provider [provider-id]
  "Set a email service provider as default - [provider-id]"
  (do-put (Request. "esp_accounts/set_default" nil {:esp_id provider-id})))

(defn get-customer [email]
  "Gets a specific customer by email - [email-address]"
  (do-get (Request. (str "customers/" email) nil nil)))

(defn upsert-customer
  "Inserts or updates a customer - [email-address]
   Inserts or updates a customer with data - [email-address customer-data]
   Inserts or updates a customer with data and locale - [email-address customer-data locale]"
  ([email] (do-post (Request. "customers" nil {:email email})))
  ([email data] (do-post (Request. "customers" nil {:email email :data data})))
  ([email data locale] (do-post (Request. "customers" nil {:email email :data data :locale locale}))))

(defn delete-customer [email]
  "Deletes and existing customer"
  (do-delete (Request. (str "customers/" email) nil nil)))

(defn get-customer-logs [email]
  "Gets a specific customer's logs by email - [email-address]"
  (do-get (Request. (str "customers/" email "/logs") nil nil)))

(defn add-customer-to-group [email group-id]
  "Adds a specific email to a group - [email-address group-id]"
  (do-post (Request. (str "customers/" email "/groups/" group-id) nil nil)))

(defn remove-customer-from-group [email group-id]
  "Removes a specific customer from a group - [email-address group-id]"
  (do-delete (Request. (str "customers/" email "/groups/" group-id) nil nil)))

(defn remove-customer-from-campaigns
  "Removes a customer for all campaigns - [email-address]
   Removes a customer from a specific campaign - [email-address campaign-id]"
  ([email] (do-post (Request. "drip_campaigns/deactivate" nil {:recipient_address email})))
  ([email drip-campaign-id] (do-post (Request. (str "drip_campaigns/" drip-campaign-id "/deactivate") nil {:recipient_address email}))))

(defn add-customer-to-campaign [drip-campaign]
  "Adds a customer to a campaign - [DripCampaign.]"
  (let [drip-campaign-id (:drip_campaign_id drip-campaign)]
    (do-post (Request. (str "drip_campaigns/" drip-campaign-id "/activate") nil drip-campaign))))

(defn get-drip-campaigns
  "Gets all drip campaigns - no args
   Gets a specific drip campaign - [campaign-id]"
  ([] (do-get (Request. "drip_campaigns" nil nil)))
  ([drip-campaign-id] (do-get (Request. (str "drip_campaigns/" drip-campaign-id) nil nil))))

(defn get-segments []
  "Gets all segments - no args"
  (do-get (Request. "segments" nil nil)))

(defn send-to-segement [segment-id template-id email-data]
  "Sends a specific segment - [segment-id template-id email-data]"
  (do-post (Request. (str "segments/" segment-id "/send") nil {:email_id template-id :email_data email-data})))

(defn get-groups []
  "Gets all groups - no args"
  (do-get (Request. "groups" nil nil)))

(defn create-group [name description]
  "Creates a new group - [name description]"
  (do-post (Request. "groups" nil {:name name :description description})))

(defn update-group [group-id name description]
  "Updates an existing group - [group-id name description]"
  (do-put (Request. (str "groups/" group-id) nil {:name name :description description})))

(defn delete-group [group-id]
  "Deletes an existing group - [group-id]"
  (do-delete (Request. (str "groups/" group-id) nil nil)))

(defn batch-send [emails]
  "Sends multiple requests - [[Email.]]"
  (let [batch-requests (vec (map #(assoc {} :path batch-path :method batch-method :body %) emails))]
    (do-post (Request. "batch" nil batch-requests))))
