(ns example.func) 

(defn parting
  "returns a String parting"
  [name]
  (str "Goodbye, " name)) ; concatenation

(println (parting "Mark")) ; -> Goodbye, Mark

;;函数的参数个数可以是不定的。可选的那些参数必须放在最后面(这一点跟其它语言是一样的), 你可以通过加个&符号把它们收集到一个list里面去
(defn power [base & exponents]
             (reduce #(Math/pow %1 %2) base exponents))
  (power 2 3 4) ; 2 to the 3rd = 8; 8 to the 4th = 4096

;; 函数定义可以包含多个参数列表以及对应的方法体。每个参数列表必须包含不同个数的参数。这通常用来给一些参数指定默认值。
(defn parting
  "returns a String parting in a given language"
  ([] (parting "World"))
  ([name] (parting name "en"))
  ([name language]
    ; condp is similar to a case statement in other languages.
    ; It is described in more detail later.
    ; It is used here to take different actions based on whether the
    ; parameter "language" is set to "en", "es" or something else.
    (condp = language
      "en" (str "Goodbye, " name)
      "es" (str "Adios, " name)
      (throw (IllegalArgumentException.
        (str "unsupported language " language))))))
 
(println (parting)) ; -> Goodbye, World
(println (parting "Mark")) ; -> Goodbye, Mark
(println (parting "Mark" "es")) ; -> Adios, Mark
;;(println (parting "Mark", "xy"))
; -> java.lang.IllegalArgumentException: unsupported language xy


(def years [1940 1944 1961 1985 1987])
;; 通过fn 定义的匿名函数可以包含任意个数的表达式； 
(filter (fn [year] (even? year)) years) ; long way w/ named arguments -> (1940 1944)
;; 而通过#(...), 定义的匿名函数则只能包含一个表达式，如果你想包含多个表达式，那么把它用do包起来。如果只有一个参数， 
;; 那么你可以通过%来引用它； 如果有多个参数， 那么可以通过%1,%2 等等来引用。 
(filter #(even? %) years) ; short way where % refers to the argument

(defn pair-test [test-fn n1 n2]
  (if (test-fn n1 n2) "pass" "fail"))
; Use a test-fn that determines whether
; the sum of its two arguments is an even number.
(println (pair-test #(even? (+ %1 %2)) 3 5)) ;;pass

;; 宏defmulti 和defmethod 经常被用在一起来定义 multimethod 

;; 宏defmulti 的参数包括一个方法名以及一个dispatch函数，这个dispatch函数的返回值会被用来选择到底调用哪个重载的函数
(defmulti what-am-i class) ; class is the dispatch function 
;; 宏defmethod 的参数则包括方法名，dispatch的值， 参数列表以及方法体。
;; defmethod 多定义的名字一样的方法，它们的参数个数必须一样。传给multimethod的参数会传给dipatch函数的。
;; 可以写你自己的dispatch函数。比如一个自定义的dispatch函数可以会根据一个东西的尺寸大小来返回:small,:medium 以及:large。然后对应每种尺寸有一个方法。
(defmethod what-am-i Number [arg] (println arg "is a Number"))
(defmethod what-am-i String [arg] (println arg "is a String"))
(defmethod what-am-i :default [arg] (println arg "is something else"))
(what-am-i 19) ; -> 19 is a Number
;;一个特殊的dispatch值:default 是用来表示默认情况的 — 即如果其它的dispatch值都不匹配的话，那么就调用这个方法
(what-am-i "Hello") ; -> Hello is a String
(what-am-i true) ; -> true is something else 

;; 下划线可以用来作为参数占位符 _ 如果你不要使用这个参数的话。
(defn callback1 [n1 n2 n3] (+ n1 n2 n3)) ; uses all three arguments
(defn callback2 [n1 _ n3] (+ n1 n3)) ; only uses 1st &amp; 3rd arguments
(defn caller [callback value]
  (callback (+ value 1) (+ value 2) (+ value 3)))
(caller callback1 10) ; 11 + 12 + 13 -> 36
(caller callback2 10) ; 11 + 13 -> 24

;; complement 函数接受一个函数作为参数，如果这个参数返回值是true， 那么它就返回false, 相当于一个取反的操作 
(defn teenager? [age] (and (>= age 13) (< age 20))) 
(def non-teen? (complement teenager?))
(println (non-teen? 47)) ; -> true


(defn times2 [n] (* n 2))
(defn minus3 [n] (- n 3))
; Note the use of def instead of defn because comp returns
; a function that is then bound to "my-composition".
(def my-composition (comp minus3 times2))
(my-composition 4) ;; (minus3 (times2  4)) ->5 

; Note the use of def instead of defn because partial returns
; a function that is then bound to "times2".
(def times3 (partial * 2))
(times3 3 4) ; 2 * 3 * 4 -> 24

(defn- polynomial
  "computes the value of a polynomial
   with the given coefficients for a given value x"
  [coefs x]
  ; For example, if coefs contains 3 values then exponents is (2 1 0).
  (let [exponents (reverse (range (count coefs)))]
    ; Multiply each coefficient by x raised to the corresponding exponent
    ; and sum those results.
    ; coefs go into %1 and exponents go into %2.
    (apply + (map #(* %1 (Math/pow x %2)) coefs exponents))))
 
(defn- derivative
  "computes the value of the derivative of a polynomial
   with the given coefficients for a given value x"
  [coefs x]
  ; The coefficients of the derivative function are obtained by
  ; multiplying all but the last coefficient by its corresponding exponent.
  ; The extra exponent will be ignored.
  (let [exponents (reverse (range (count coefs)))
        derivative-coefs (map #(* %1 %2) (butlast coefs) exponents)]
    (polynomial derivative-coefs x)))
 
(def f (partial polynomial [2 1 3])) ; 2x^2 + x + 3
(def f-prime (partial derivative [2 1 3])) ; 4x + 1
 
(println "f(2) =" (f 2)) ; -> 13.0
(println "f'(2) =" (f-prime 2)) ; -> 9.0

(defn- polynomial
  "computes the value of a polynomial
   with the given coefficients for a given value x"
  [coefs x]
  (reduce #(+ (* x %1) %2) coefs)) 


; Note the use of def instead of defn because memoize returns
; a function that is then bound to "memo-f".
(def memo-f (memoize f))
 
(println "priming call")
(time (f 2))
 
(println "without memoization")
; Note the use of an underscore for the binding that isn't used.
(dotimes [_ 3] (time (f 2)))
 
(println "with memoization")
(dotimes [_ 3] (time (memo-f 2)))
