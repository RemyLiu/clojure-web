(import
  '(java.util Calendar GregorianCalendar)
  '(javax.swing JFrame JLabel))

;;static attribute
(. java.util.Calendar APRIL) ; -> 3
(. Calendar APRIL) ; works if the Calendar class was imported
java.util.Calendar/APRIL
Calendar/APRIL ; works if the Calendar class was imported

;; static method
(. Math pow 2 4) ; -> 16.0
(Math/pow 2 4)  

;; create new object 
(def calendar (new GregorianCalendar 2008 Calendar/APRIL 16)) ; April 16, 2008
(def calendar (GregorianCalendar. 2008 Calendar/APRIL 16)) 

(. calendar add Calendar/MONTH 2)
(. calendar get Calendar/MONTH) ; -> 5
(.add calendar Calendar/MONTH 2)
(.get calendar Calendar/MONTH) ; -> 7

(. (. calendar getTimeZone) getDisplayName) ; long way
;; 方法调用可以用.. 宏串起来, 
;; .?. 在clojure.contrib.core ，在调用的过程中如果有一个返回结果是nil, 它就不再继续调用了，可以防止出现NullPointerException异常。
(.?. calendar getTimeZone getDisplayName) ; -> "China Standard Time" 

;;doto 函数可以用来调用一个对象上的多个方法。它返回它的第一个参数， 也就是所要调用方法的对象。
(doto calendar
  (.set Calendar/YEAR 1981)
  (.set Calendar/MONTH Calendar/AUGUST)
  (.set Calendar/DATE 1))
(def formatter (java.text.DateFormat/getDateInstance))
(.format formatter (.getTime calendar)) ; -> "Aug 1, 1981"

(println (map #(.substring %1 %2)
           ["Moe" "Larry" "Curly"] [1 2 3])) ; -> (oe rry ly)
(println (map (memfn substring beginIndex)
           ["Moe" "Larry" "Curly"] [1 2 3])) ; -> same

(defn delayed-print [ms text]
  (Thread/sleep ms)
  (println text))
 
; Pass an anonymous function that invokes delayed-print
; to the Thread constructor so the delayed-print function
; executes inside the Thread instead of
; while the Thread object is being created.
(.start (Thread. #(delayed-print 1000 ", World!"))) ; prints 2nd
(print "Hello") ; prints 1st
; output is "Hello, World!"

;;
(defn collection? [obj]
  (println "obj is a" (class obj))
  ; Clojure collections implement clojure.lang.IPersistentCollection.
  (or (coll? obj) ; Clojure collection?
      (instance? java.util.Collection obj))) ; Java collection?
 
(defn average [coll]
  (when-not (collection? coll)
    (throw (IllegalArgumentException. "expected a collection")))
  (when (empty? coll)
    (throw (IllegalArgumentException. "collection is empty")))
  ; Apply the + function to all the items in coll,
  ; then divide by the number of items in it.
  (let [sum (apply + coll)]
    (/ sum (count coll))))
 
(try
  (println "list average =" (average '(2 3))) ; result is a clojure.lang.Ratio object
  (println "vector average =" (average [2 3])) ; same
  (println "set average =" (average #{2 3})) ; same
  (let [al (java.util.ArrayList.)]
    (doto al (.add 2) (.add 3))
    (println "ArrayList average =" (average al))) ; same
  (println "string average =" (average "1 2 3 4")) ; illegal argument
  (catch IllegalArgumentException e
    (println e)
    ;(.printStackTrace e) ; if a stack trace is desired
  )
  (finally
    (println "in finally")))
