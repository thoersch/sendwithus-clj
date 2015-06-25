sendwithus-clj: Clojure SDK for the [sendwithus API](https://www.sendwithus.com/docs/api/)
===========

This library provides a easy to use interface for integrating the sendwithus for sending and managing all aspects of email transactions.

## Installation

The sendwithus-clj library can be installed as a dependency from [Clojars](https://clojars.org/sendwithus-clj)

```el
    [sendwithus-clj "1.0.0-SNAPSHOT"]
```

Or gradle

    compile "sendwithus-clj:sendwithus-clj:1.0.0-SNAPSHOT"

Or maven

    <dependency>
      <groupId>sendwithus-clj</groupId>
      <artifactId>sendwithus-clj</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency

## Usage

```el
(use 'sendwithus-clj.core)
```

The library provide a simple macro (with-send-with-us [api-key]) that uses your sendwithus api key to wrap all request to the api.

Example:

```el
; gets all drip campaigns
(with-send-with-us "api-key-here"
  (get-drip-campaigns))
;=> [{:enabled false, :id dc_HNiKwoKo44UnVnhf9iBmHX, :name My Campaign, :trigger_email_id nil, :drip_steps [{:delay_seconds 604800, :id dcs_TXb27jmMpSv9V3497iHdaL, :email_id tem_B76FSNaAtKYYeqnALoBYVh, :object drip_step}], :object drip_campaign}]
```

The library also exposes record constructs that make more rich payloads easy:

```el
(defrecord Template [id locale version name subject html text])
(defrecord Recipient [address name])
(defrecord Sender [address reply_to name])
(defrecord Email [email_id recipient cc bcc sender email_data tags headers inline files esp_account locale version_name])
(defrecord RenderRequest [template_id template_data version_id version_name locale strict])
(defrecord DripCampaign [drip_campaign_id recipient cc bcc sender email_data tags esp_account locale])
```

These can be used directly with the library:

```el
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
```

## Examples

All api examples can be found in the sendwithus-clj.examples namespace

## Questions or Comments or Bugs

* email: thoersch@gmail.com

If you come across any issues, please file them on the [Github project issue tracker](https://github.com/thoersch/sendwithus-clj/issues).

## Documentation

Check out [sendwithus-clj docs](http://thoersch.github.io/sendwithus-clj/)

or

Check out the full [SendWithUs API Documentation](https://www.sendwithus.com/docs/api/).

## License

Copyright © 2015 Tyler Hoersch

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
