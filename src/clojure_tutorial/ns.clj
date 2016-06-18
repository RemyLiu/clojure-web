(ns com.example.demo
 ;; (:require 1)
  ; assumes this dependency: [org.clojure/math.numeric-tower "0.0.1"]
  ;;(:use 1)
  (:import 
   (java.text NumberFormat) 
   (javax.swing JFrame JLabel)))
;; 符号(Symbols)是用来给函数、宏以及binding来分配名字的。符号被划分到名字空间里面去了。 
;; 任何时候总有一个默认的名字空间，初始化的时候这个默认的名字空间是“user”，这个默认的名字空间的值被保存在特殊符号*ns*.里面。默认的名字空间可以通过两种方法来改变。in-ns 函数只是改变它而已. 而ns 宏则做得更多。其中一件就是它会使得clojure.core 名字空间里面的符号在新的名字空间里面都可见 (使用refer 命令). 

;; require 函数可以加载 Clojure 库 
(require 'clojure.string) 
(clojure.string/join "$" [1 2 3]) 

;; alias 函数给一个名字空间指定一个别名
(alias 'su 'clojure.string)
(su/join "$" [1 2 3]) ; -> "1$2$3" 

;; refer函数使得指定的名字空间里面的函数在当前名字空间里面可以访问
;; 果当前名字空间有那个名字空间一样的名字， 那么访问的时候还是要指定名字空间的
(refer 'clojure.string)  
(join "$" [1 2 3]) 

;; use 相当于require和refer 
(use 'clojure.string)


 
(println (su/join "$" [1 2 3])) ; -> 1$2$3
;;(println (gcd 27 72)) ; -> 9
;;(println (sqrt 5)) ; -> 2.23606797749979
(println (.format (NumberFormat/getInstance) Math/PI)) ; -> 3.142
 
; See the screenshot that follows this code.
(doto (JFrame. "Hello")
  (.add (JLabel. "Hello, World!"))
  (.pack)
  (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
  (.setVisible true))


(def foo 1)
;;create-ns函数可以创建一个新的名字空间。但是不会把它变成默认的名字空间
(create-ns 'com.klose.demo) 
;; intern函数在一个指定名字空间里面定义一个符号(如果这个符号不存在的话) ， 同时还可以给它指定一个默认值  
(intern 'com.klose.demo 'foo 2)
(println (+ foo com.klose.demo/foo)) ; -> 3
;; ns-interns 函数返回一个指定的名字空间的所有的符号的map(这个名字空间一定要在当前名字空间里面加载了), 这个map的key是符号的名字， value是符号所对应的Var 对象， 这个对象表示的可能是函数，宏或者binding。 
(ns-interns 'com.klose.demo) ;; -> {foo #'com.klose.demo/foo}
;; all-ns 函数返回一个包含当前所有的已经加载了的名字空间的集合 
;; namespace 函数返回一个给定符号或者关键字的名字空间 

;;Symbol 对象有一个String 类型的名字以及一个String 类型的名字空间名字(叫做ns), 但是没有值。它使用一个字符串的名字空间而不是一个名字空间对象使得它可以指向一个还不存在的名字空间。Var 对象有一个执行Symbol 对象的引用 (叫做sym), 一个指向Namespace对象的引用 (叫做ns) 以及一个Object 类型的对象(也就是它的root value, 叫做root). Namespace对象bjects有一个指向Map 的引用， 这个map维护Symbol 对象和Var 对象的对应关系 (叫做mappings)。同时它还有一个map来维护Symbol 别名和Namespace 对象之间的关系 (叫做namespaces). 下面这个类图显示了Java里面的类和接口在Clojure里面的实现。在Clojure里面 "interning" 这个单词一般指的是添加一个Symbol到Var 的对应关系到一个Namespace里面去。

;; ns, require, ref, use, import ...
