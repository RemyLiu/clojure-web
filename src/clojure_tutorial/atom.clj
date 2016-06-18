;; 同步对于一个值的修改, 所有线程可见 

(def my-atom (atom 1)) ;; atom函数定义一个atom引用
(reset! my-atom 2) ;; reset!改变值
(println @my-atom) ;; -> 2 

;; compare-and-set! 函数接受三个参数：要被修改的Atom, 上次读取时候的值，新的值。 
;; 这个函数在设置新值之前会去读Atom现在的值。如果与上次读的时候的值相等， 那么设置新值并返回true, 否则不设置新值， 返回false
(reset! my-atom 1)
(defn update-atom []
  (let [curr-val @my-atom]
    (println "update-atom: curr-val =" curr-val) ; -> 1
    (Thread/sleep 50) ; give reset! time to run
    (println
     (compare-and-set! my-atom curr-val (inc curr-val))))) ; -> false

(let [thread (Thread. #(update-atom))]
  (.start thread)
  (Thread/sleep 25) ; give thread time to call update-atom 
  (reset! my-atom 3) ; happens after update-atom binds curr-val
  (.join thread)) ; wait for thread to finish

(println @my-atom) ; -> 3 


(reset! my-atom 1)
(defn update-atom [curr-val]
  (println "update-atom: curr-val =" curr-val)
  (Thread/sleep 50) ; give reset! time to run
  (inc curr-val))

;; swap! 函数接受一个要修改的 Atom, 一个计算Atom新值的函数以及一些额外的参数(如果需要的话)。这个计算Atom新的值的函数会以这个Atom以及一些额外的参数做为输入。swap！函数实际上是对compare-and-set!函数的一个封装，但是有一个显著的不同。 它首先把Atom的当前值存入一个变量，然后调用计算新值的函数来计算新值， 然后再调用compare-and-set!函数来赋值。如果赋值成功的话，那就结束了。如果赋值不成功的话， 那么它会重复这个过程，一直到赋值成功为止。 
(let [thread (Thread. #(swap! my-atom update-atom))]
  (.start thread)
  (Thread/sleep 25) ; give swap! time to call update-atom
  (reset! my-atom 3)
  (.join thread)) ; wait for thread to finish
(println @my-atom) ; -> 4
