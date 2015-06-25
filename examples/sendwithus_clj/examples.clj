(ns sendwithus-clj.examples
  (:use [sendwithus-clj.core]))

; Sending an email
(with-send-with-us "api-key-here"
    (send-email (Email.
                  "tem_B76FSNaAtKYYeqnALoBYVh"
                  (Recipient. "recipient@test.com" "recipient test")
                  [(Recipient. "cc-test1@test.com" "cc test")]
                  [(Recipient. "bcc-test1@test.com" "bcc test")]
                  (Sender. "swu-clj-client@test.com" "noreply@test.com" "SWU clj")
                  {:amount "$12.99"}
                  ["tag1" "tag2"]
                  {:X-HEADER-ONE "custom header"}
                  {:id "inline-message" :data "SGkgdGhpcyBpcyBhIG1lc3NhZ2U="}
                  [{:id "doc.txt" :data "SGVsbG8sIHRoaXMgaXMgYSB0ZXh0IGZpbGUuCg=="}]
                  "esp_1a2b3c4d5e"
                  "en-US"
                  "Version")))
;=> {:success true, :status OK, :receipt_id log_37f0270870c138f526cace50b7615f6c, :email {:locale en-US, :version_name Version, :name My Template}}

; resends an email, using log id
(with-send-with-us "api-key-here"
  (resend-email "log_37f0270870c138f526cace50b7615f6c"))
;=> {:receipt_id log_37f0270870c138f526cace50b7615f6c, :email {:version_name Version, :name New Template, :locale en-US}, :success true, :status OK}

; gets all logs
(with-send-with-us "api-key-here"
  (get-logs))

; gets log entry with events
(with-send-with-us "api-key-here"
  (get-logs "log_37f0270870c138f526cace50b7615f6c" true))

;add version to a template
(with-send-with-us "api-key-here"
  (add-version
    (Template. "tem_B76FSNaAtKYYeqnALoBYVh" "ru-RU" nil "Version" "My Spanish Subject" "<html><head><title></title></head><body>hola!</body></html>" "hola")))

; gets all snippets
(with-send-with-us "api-key-here"
  (get-snippets))
;=> [{:object snippet, :body <div>Hello, I'm a snippet.</div>, :id snp_KnQzcVCU4nddskYcGSxE4m, :name My Snippet, :created 1434808837}]

; gets a specific snippet
(with-send-with-us "api-key-here"
  (get-snippets "snp_KnQzcVCU4nddskYcGSxE4m"))
;=> {:created 1434808837, :name My Snippet, :object snippet, :body <div>Hello, I'm a snippet.</div>, :id snp_KnQzcVCU4nddskYcGSxE4m}

; creates a snippet
(with-send-with-us "api-key-here"
  (create-snippet "New Snippet" "<p>My New Snippet!</p>"))
;=> {:success true, :snippet {:name New Snippet, :created 1434811290, :id snp_hctHQMTP9tBKRqAZawmTPQ, :body <p>My New Snippet!</p>, :object snippet}, :status OK}

; updates a snippet
(with-send-with-us "api-key-here"
  (update-snippet "snp_hctHQMTP9tBKRqAZawmTPQ" "Updated Snippet" "<p>My Updated Snippet!</p>"))
;=> {:success true, :snippet {:name Updated Snippet, :created 1434811290, :id snp_hctHQMTP9tBKRqAZawmTPQ, :body <p>My Updated Snippet!</p>, :object snippet}, :status OK}

; deletes a snippet
(with-send-with-us "api-key-here"
  (delete-snippet "snp_hctHQMTP9tBKRqAZawmTPQ"))
;=> {:success true, :status OK}

; renders a template
(with-send-with-us "api-key-here"
  (render-email (RenderRequest. "tem_B76FSNaAtKYYeqnALoBYVh" {:amount "$12.99"} "ver_3aVkkbePDvaq5L72vWmWs4" "New Version" "en-US" true)))
;=> {:template {:id tem_B76FSNaAtKYYeqnALoBYVh, :locale en-US, :name New Template, :version_name New Version}, :success true, :subject My Subject, :status OK, :html <html><head><title></title></head><body>new!</body></html>, :text new}

; gets all email service providers
(with-send-with-us "api-key-here"
  (get-providers))
;=> [{:is_default true, :object esp_account, :id esp_3R5MA4RfoTxNW3hQ7pUoPG, :name mandrill, :esp_type mandrill, :created 1434821141}]

; sets the default service provider
(with-send-with-us "api-key-here"
  (set-default-provider "esp_3R5MA4RfoTxNW3hQ7pUoPG"))
;=> {:success true, :esp_account {:esp_type mandrill, :name mandrill, :id esp_3R5MA4RfoTxNW3hQ7pUoPG, :object esp_account, :created 1434821141, :is_default true}, :status OK}

; gets a specific customer
(with-send-with-us "api-key-here"
  (get-customer "recipients@test.com"))
;=> {:success true, :status OK, :customer {:email thoersch@gmail.com, :data {:amount $12.99}, :object customer, :groups [], :created 1434806281, :locale en-US}}

; update or insert a customer
(with-send-with-us "api-key-here"
  (upsert-customer "recipients@test.com" {:name "Billy Somebody" :state "Wisconsin"} "en-US"))
;=> {:success true, :status OK}

; deletes a customer
(with-send-with-us "api-key-here"
  (delete-customer "recipients@test.com"))
;=> {:status OK, :success true}

; gets a customer's email log
(with-send-with-us "api-key-here"
  (get-customer-logs "recipients@test.com"))
;=> {:status OK, :success true, :logs [{:status re_opened, :email_version Version, :object log, :created 1434806470, :message SendGrid: Recipient has opened the HTML message., :email_id tem_B76FSNaAtKYYeqnALoBYVh, :email_name New Template, :recipient_address recipients@test.com, :id log_37f0270870c138f526cace50b7615f6c, :recipient_name Billy Somebody} {:status opened, :email_version New Version, :object log, :created 1434806273, :message SendGrid: Recipient has opened the HTML message., :email_id tem_B76FSNaAtKYYeqnALoBYVh, :email_name New Template, :recipient_address recipients@test.com, :id log_2210b76c90220f4fd429a9dc650cac50, :recipient_name Billy Somebody}]}

; adds a customer to group
(with-send-with-us "api-key-here"
  (add-customer-to-group "recipients@test.com" "grp_ynYBhuxnfRYFV3vcRvFZXQ"))
;=> {:success true, :status OK}

; removes a customer from a group
(with-send-with-us "api-key-here"
  (remove-customer-from-group "recipients@test.com" "grp_ynYBhuxnfRYFV3vcRvFZXQ"))
;=> {:success true, :status OK}

; gets all drip campaigns
(with-send-with-us "api-key-here"
  (get-drip-campaigns))
;=> [{:enabled false, :id dc_HNiKwoKo44UnVnhf9iBmHX, :name My Campaign, :trigger_email_id nil, :drip_steps [{:delay_seconds 604800, :id dcs_TXb27jmMpSv9V3497iHdaL, :email_id tem_B76FSNaAtKYYeqnALoBYVh, :object drip_step}], :object drip_campaign}]

; gets a specific drip campaign
(with-send-with-us "api-key-here"
  (get-drip-campaigns "dc_HNiKwoKo44UnVnhf9iBmHX"))
;=> {:drip_steps [{:email_id tem_B76FSNaAtKYYeqnALoBYVh, :object drip_step, :delay_seconds 604800, :id dcs_TXb27jmMpSv9V3497iHdaL}], :enabled false, :object drip_campaign, :name My Campaign, :id dc_HNiKwoKo44UnVnhf9iBmHX, :trigger_email_id nil}

; removes email from all drip campaigns
(with-send-with-us "api-key-here"
  (remove-customer-from-campaigns "recipients@test.com"))
;=> {:success true, :status OK, :recipient_address recipients@test.com}

; removes email from specific drip campaign
(with-send-with-us "api-key-here"
  (remove-customer-from-campaigns "recipients@test.com" "dc_HNiKwoKo44UnVnhf9iBmHX"))
;=> {:success true, :status OK, :drip_campaign {:name My Campaign, :id dc_HNiKwoKo44UnVnhf9iBmHX}, :message Recipient successfully removed from drip campaign., :recipient_address recipients@test.com}

; adds a customer to a specific drip campaign
(with-send-with-us "api-key-here"
  (add-customer-to-campaign (DripCampaign. "dc_HNiKwoKo44UnVnhf9iBmHX"
                              (Recipient. "recipients@test.com" "Billy Somebody")
                              [(Recipient. "cc-test1@test.com" "cc test")]
                              [(Recipient. "bcc-test1@test.com" "bcc test")]
                              (Sender. "swu-clj-client@gmail.com" "noreply@test.com" "SWU clj")
                              {:amount "$12.99"}
                              ["tag1" "tag2"]
                              "esp_1a2b3c4d5e"
                              "en-US")))
;=> {:success true, :status OK, :drip_campaign {:name My Campaign, :id dc_HNiKwoKo44UnVnhf9iBmHX}, :message Recipient successfully added to drip campaign., :recipient_address recipients@test.com}

; gets all segments
(with-send-with-us "api-key-here"
  (get-segments))
;=> [{:created 1434572374, :id seg_9KAgTPqo5PybwJXTmNCp2H, :name Clicks - Last 30 Days, :object segment} {:created 1434572374, :id seg_wPjBETrofN8TFutqPGFHCD, :name Opens - Last 7 Days, :object segment} {:created 1434572374, :id seg_4DE3YwfPNRQBUtB7Xenx9k, :name All Customers, :object segment}]

; send to segment
(with-send-with-us "api-key-here"
  (send-to-segement "seg_4DE3YwfPNRQBUtB7Xenx9k" "tem_B76FSNaAtKYYeqnALoBYVh" {:amount "$12.99"}))
;=> {:success true, :status OK}

; create a group
(with-send-with-us "api-key-here"
  (create-group "My Group" "This is my awesome group"))
;=> {:group {:description This is my awesome group, :id grp_VjZdRPpky6W2icXM4ufNiG, :name My Group}, :status OK, :success true}

; get all groups
(with-send-with-us "api-key-here"
  (get-groups))
;=> {:groups [{:description This is my awesome group, :id grp_VjZdRPpky6W2icXM4ufNiG, :name My Group} {:description , :id grp_ynYBhuxnfRYFV3vcRvFZXQ, :name My Test Group}], :status OK, :success true}

; update a specific group
(with-send-with-us "api-key-here"
  (update-group "grp_VjZdRPpky6W2icXM4ufNiG" "New Group Name" "New Description"))
;=> {:group {:description New Description, :id grp_VjZdRPpky6W2icXM4ufNiG, :name New Group Name}, :status OK, :success true}

; delete a group
(with-send-with-us "api-key-here"
  (delete-group "grp_VjZdRPpky6W2icXM4ufNiG"))
;=> {:success true, :status OK}

; send emails in a batch request
(with-send-with-us "api-key-here"
  (batch-send [(Email.
                 "tem_B76FSNaAtKYYeqnALoBYVh"
                 (Recipient. "test1@test.com" "recipient test")
                 [(Recipient. "cc-test1@test.com" "cc test")]
                 [(Recipient. "bcc-test1@test.com" "bcc test")]
                 (Sender. "swu-clj-client@test.com" "noreply@test.com" "SWU clj")
                 {:amount "$12.99"}
                 ["tag1" "tag2"]
                 {:X-HEADER-ONE "custom header"}
                 {:id "inline-message" :data "SGkgdGhpcyBpcyBhIG1lc3NhZ2U="}
                 [{:id "doc.txt" :data "SGVsbG8sIHRoaXMgaXMgYSB0ZXh0IGZpbGUuCg=="}]
                 "esp_1a2b3c4d5e"
                 "en-US"
                 "Version")
               (Email.
                 "tem_B76FSNaAtKYYeqnALoBYVh"
                 (Recipient. "test2@test.com" "recipient 2 test")
                 [(Recipient. "cc-test1@test.com" "cc test")]
                 [(Recipient. "bcc-test1@test.com" "bcc test")]
                 (Sender. "swu-clj-client@test.com" "noreply@test.com" "SWU clj")
                 {:amount "$12.99"}
                 ["tag1" "tag2"]
                 {:X-HEADER-ONE "custom header"}
                 {:id "inline-message" :data "SGkgdGhpcyBpcyBhIG1lc3NhZ2U="}
                 [{:id "doc.txt" :data "SGVsbG8sIHRoaXMgaXMgYSB0ZXh0IGZpbGUuCg=="}]
                 "esp_1a2b3c4d5e"
                 "en-US"
                 "Version")
               (Email.
                 "tem_B76FSNaAtKYYeqnALoBYVh"
                 (Recipient. "test3@test.com" "recipient 3 test")
                 [(Recipient. "cc-test1@test.com" "cc test")]
                 [(Recipient. "bcc-test1@test.com" "bcc test")]
                 (Sender. "swu-clj-client@test.com" "noreply@test.com" "SWU clj")
                 {:amount "$12.99"}
                 ["tag1" "tag2"]
                 {:X-HEADER-ONE "custom header"}
                 {:id "inline-message" :data "SGkgdGhpcyBpcyBhIG1lc3NhZ2U="}
                 [{:id "doc.txt" :data "SGVsbG8sIHRoaXMgaXMgYSB0ZXh0IGZpbGUuCg=="}]
                 "esp_1a2b3c4d5e"
                 "en-US"
                 "Version")]))
;=> [{:body {:status OK, :email {:locale en-US, :name New Template, :version_name Version}, :receipt_id log_48461b16159187effff3ef83f19c6c2a, :success true}, :method POST, :status_code 200, :path /api/v1/send} {:body {:status OK, :email {:locale en-US, :name New Template, :version_name Version}, :receipt_id log_9c81a1214c219e50bb89f90b937b1625, :success true}, :method POST, :status_code 200, :path /api/v1/send} {:body {:status OK, :email {:locale en-US, :name New Template, :version_name Version}, :receipt_id log_e13886982e4784ff38953a29e2209a37, :success true}, :method POST, :status_code 200, :path /api/v1/send}]
