(ns inflections.test.util
  (:require [inflections.util :refer [parse-bytes parse-double parse-float parse-integer]]
            [inflections.util :refer [parse-long parse-location parse-url]]))

(defn test-parse-bytes []
  (assert (nil? (parse-bytes nil)))
  (assert (nil? (parse-bytes "")))
  (assert (= 1 (parse-bytes "1")))
  (assert (= 1.0 (parse-bytes "1B")))
  (assert (= 1.0 (parse-bytes "1.0B")))
  (assert (= 10.0 (parse-bytes "10.0")))
  (assert (= -10.0 (parse-bytes "-10.0")))
  (assert (= 1024.0 (parse-bytes "1K")))
  (assert (= 1048576.0 (parse-bytes "1M")))
  (assert (= 1048576.0 (parse-bytes "1.0M"))))

(defn test-parse-double []
  (assert (nil? (parse-double nil)))
  (assert (nil? (parse-double "")))
  (assert (= 1.0 (parse-double "1")))
  (assert (= 10.0 (parse-double "10.0")))
  (assert (= -10.0 (parse-double "-10.0")))
  (assert (= 1000000.0 (parse-double "1M")))
  (assert (= 1000000.0 (parse-double "1.0M")))
  (assert (= 1000000000.0 (parse-double "1B")))
  (assert (= 1000000000.0 (parse-double "1.0B"))))

(defn test-parse-float []
  (assert (nil? (parse-float nil)))
  (assert (nil? (parse-float "")))
  (assert (= 1.0 (parse-float "1")))
  (assert (= 10.0 (parse-float "10.0")))
  (assert (= -10.0 (parse-float "-10.0")))
  (assert (= 1000000.0 (parse-float "1M")))
  (assert (= 1000000.0 (parse-float "1.0M")))
  (assert (= 1000000000.0 (parse-float "1B")))
  (assert (= 1000000000.0 (parse-float "1.0B"))) )

(defn test-parse-integer []
  (assert (nil? (parse-integer nil)))
  (assert (nil? (parse-integer "")))
  (assert (= 1 (parse-integer "1.1")))
  (assert (= 1 (parse-integer "1")))
  (assert (= 1 (parse-integer "1-europe")))
  (assert (= 10 (parse-integer "10")))
  (assert (= -10 (parse-integer "-10")))
  (assert (= 1000000 (parse-integer "1M")))
  (assert (= 1000000000 (parse-integer "1B"))))

(defn test-parse-long []
  (assert (nil? (parse-long nil)))
  (assert (nil? (parse-long "")))
  (assert (= 1 (parse-long "1.1")))
  (assert (= 1 (parse-long "1")))
  (assert (= 1 (parse-long "1-europe")))
  (assert (= 10 (parse-long "10")))
  (assert (= -10 (parse-long "-10")))
  (assert (= 1000000 (parse-long "1M")))
  (assert (= 1000000000 (parse-long "1B"))))

(defn test-parse-location []
  (assert (nil? (parse-location nil)))
  (assert (nil? (parse-location "")))
  (assert (nil? (parse-location "a,b")))
  (assert (= {:latitude 1.0 :longitude -2.0} (parse-location "1,-2")))
  (assert (= {:latitude 1.0 :longitude -2.0} (parse-location "1.0,-2.0")))
  (assert (= {:latitude 1.0 :longitude -2.0} (parse-location "1.0 -2.0"))))

(defn test-parse-url []
  (let [spec (parse-url "postgresql://localhost/example")]
    (assert (= "postgresql" (:scheme spec)))
    (assert (= "localhost" (:server-name spec)))
    (assert (= "/example" (:uri spec))))
  (let [spec (parse-url "postgresql://tiger:scotch@localhost:5432/example?a=1&b=2")]
    (assert (= "postgresql" (:scheme spec)))
    (assert (= "tiger" (:user spec)))
    (assert (= "scotch" (:password spec)))
    (assert (= "localhost" (:server-name spec)))
    (assert (= 5432 (:server-port spec)))
    (assert (= "/example" (:uri spec)))
    (assert (= "a=1&b=2" (:query-string spec)))
    (assert (= {:a "1", :b "2"} (:params spec))))
  (let [spec (parse-url "rabbitmq://tiger:scotch@localhost:5672")]
    (assert (= "rabbitmq" (:scheme spec)))
    (assert (= "tiger" (:user spec)))
    (assert (= "scotch" (:password spec)))
    (assert (= "localhost" (:server-name spec)))
    (assert (= 5672 (:server-port spec)))
    (assert (nil? (:uri spec)))
    (assert (nil? (:params spec)))
    (assert (nil? (:query-string spec)))))

(defn test []
  (test-parse-bytes)
  (test-parse-double)
  (test-parse-float)
  (test-parse-integer)
  (test-parse-location)
  (test-parse-url))
