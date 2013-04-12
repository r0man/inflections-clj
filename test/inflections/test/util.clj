(ns inflections.test.util
  (:use clojure.test
        inflections.util))

(deftest test-parse-bytes
  (is (nil? (parse-bytes nil)))
  (is (nil? (parse-bytes "")))
  (is (= 1 (parse-bytes "1")))
  (is (= 1.0 (parse-bytes "1B")))
  (is (= 1.0 (parse-bytes "1.0B")))
  (is (= 10.0 (parse-bytes "10.0")))
  (is (= -10.0 (parse-bytes "-10.0")))
  (is (= 1024.0 (parse-bytes "1K")))
  (is (= 1048576.0 (parse-bytes "1M")))
  (is (= 1048576.0 (parse-bytes "1.0M"))))

(deftest test-parse-double
  (is (nil? (parse-double nil)))
  (is (nil? (parse-double "")))
  (is (= 1.0 (parse-double "1")))
  (is (= 10.0 (parse-double "10.0")))
  (is (= -10.0 (parse-double "-10.0")))
  (is (= 1000000.0 (parse-double "1M")))
  (is (= 1000000.0 (parse-double "1.0M")))
  (is (= 1000000000.0 (parse-double "1B")))
  (is (= 1000000000.0 (parse-double "1.0B"))))

(deftest test-parse-float
  (is (nil? (parse-float nil)))
  (is (nil? (parse-float "")))
  (is (= 1.0 (parse-float "1")))
  (is (= 10.0 (parse-float "10.0")))
  (is (= -10.0 (parse-float "-10.0")))
  (is (= 1000000.0 (parse-float "1M")))
  (is (= 1000000.0 (parse-float "1.0M")))
  (is (= 1000000000.0 (parse-float "1B")))
  (is (= 1000000000.0 (parse-float "1.0B"))) )

(deftest test-parse-integer
  (is (nil? (parse-integer nil)))
  (is (nil? (parse-integer "")))
  (is (nil? (parse-integer "1.1")))
  (is (= 1 (parse-integer "1")))
  (is (= 1 (parse-integer "1-europe")))
  (is (= 10 (parse-integer "10")))
  (is (= -10 (parse-integer "-10")))
  (is (= 1000000 (parse-integer "1M")))
  (is (= 1000000000 (parse-integer "1B"))))

(deftest test-parse-long
  (is (nil? (parse-long nil)))
  (is (nil? (parse-long "")))
  (is (nil? (parse-long "1.1")))
  (is (= 1 (parse-long "1")))
  (is (= 1 (parse-long "1-europe")))
  (is (= 10 (parse-long "10")))
  (is (= -10 (parse-long "-10")))
  (is (= 1000000 (parse-long "1M")))
  (is (= 1000000000 (parse-long "1B"))))

(deftest test-parse-location
  (is (nil? (parse-location nil)))
  (is (nil? (parse-location "")))
  (is (nil? (parse-location "a,b")))
  (is (= {:latitude 1.0 :longitude -2.0} (parse-location "1,-2")))
  (is (= {:latitude 1.0 :longitude -2.0} (parse-location "1.0,-2.0")))
  (is (= {:latitude 1.0 :longitude -2.0} (parse-location "1.0 -2.0"))))

(deftest test-parse-url
  (let [spec (parse-url "postgresql://localhost/example")]
    (is (= :postgresql (:scheme spec)))
    (is (= "localhost" (:server-name spec)))
    (is (= "/example" (:uri spec))))
  (let [spec (parse-url "postgresql://tiger:scotch@localhost:5432/example?a=1&b=2")]
    (is (= :postgresql (:scheme spec)))
    (is (= "tiger" (:user spec)))
    (is (= "scotch" (:password spec)))
    (is (= "localhost" (:server-name spec)))
    (is (= 5432 (:server-port spec)))
    (is (= "/example" (:uri spec)))
    (is (= "a=1&b=2" (:query-string spec)))
    (is (= {:a "1", :b "2"} (:params spec))))
  (let [spec (parse-url "rabbitmq://tiger:scotch@localhost:5672")]
    (is (= :rabbitmq (:scheme spec)))
    (is (= "tiger" (:user spec)))
    (is (= "scotch" (:password spec)))
    (is (= "localhost" (:server-name spec)))
    (is (= 5672 (:server-port spec)))
    (is (nil? (:uri spec)))
    (is (nil? (:params spec)))
    (is (nil? (:query-string spec)))))

(deftest test-parse-percent
  (are [string expected]
       (is (= expected (parse-percent string)))
       "+18.84" 18.84
       "+18.84%" 18.84))

(deftest test-pattern-quote
  (is (= "1" (pattern-quote "1")))
  (is (= "x" (pattern-quote "x")))
  (is (= "\\." (pattern-quote ".")))
  (is (= "\\[" (pattern-quote "[")))
  (is (= "a\\.b\\.c" (pattern-quote "a.b.c"))))

(deftest test-separator
  (is (nil? (separator "Message")))
  (is (= "." (separator "twitter.hash-tags")))
  (is (= "." (separator "twitter.users")))
  (is (= "::" (separator "Admin::Post"))))
