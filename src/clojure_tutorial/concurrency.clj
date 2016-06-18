(ns example.concurrency
  (:require example.func))

;; future宏把它的body里面的表达式在另外一个线程里面执行（这个线程来自于CachedThreadPool，Agents(后面会介绍)用的也是这个). 
;;这个对于那种运行时间比较长， 而且一下子也不需要运行结果的程序来说比较有用。你可以通过dereferencing 从future. 放回的对象来得到返回值。 如果计算已经结束了， 那么立马返回那个值；如果计算还没有结束，那么当前线程会block住，直到计算结束返回。
;; 所以我们要在一个适当的时机调用shutdown-agents 关闭这些线程，然后程序才能退出 

(println "creating future")
(def my-future (future (example.func/f-prime 2))) ; f-prime is called in another thread
(println "created future")
(println "result is" @my-future) 
(shutdown-agents)

;; pmap函数把一个函数作用到一个集合里面的所有的元素,  和map不一样的是这个过程是完全并行的 
;; clojure.parallel 名字空间里买你有好多方法可以帮助你并行化你的代码, 他们包括: par,pdistinct,pfilter-dupes,pfilter-nils,pmax,pmin,preduce,psort,psummary 和pvec 

;; 引用类型: 一种可变引用指向不可变数据的一种机制. Clojure里面有4种引用类型:Vars, Refs , Atoms和Agents 
;;    它们都可以指向任意类型的对象。
;;   都可以利用函数deref 以及宏@ 来读取它所指向的对象。
;;   它们都支持验证函数，这些函数在它们所指向的值发生变化的时候自动调用。如果新值是合法的值，那么验证函数简单的返回true, 如果新值是不合法的，那么要么返回false， 要么抛出一个异常。如果只是简单地返回了false, 那么一个IllegalStateException 异常会被抛出，并且带着提示信息： "Invalid reference state" 。
;;   如果是Agents的话，它们还支持watchers。如果被监听的引用的值发生了变化，那么Agent会得到通知。 

