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
