;; lazy序列(LazySeq), 这种序列里面的元素不是实际的数据， 而是一些方法， 它们直到用户真正需要数据的时候才会被调用。
;; LazySeq的一个好处是在你创建这个序列的时候你不用太担心这个序列到底会有多少元素。
;; 会返回lazySeq的一些函数:
;; cache-seq,concat,cycle,distinct,drop,drop-last,drop-while,filter,for,interleave,interpose,iterate,lazy-cat,lazy-seq,line-seq,map,partition,range,re-seq,remove,repeat,replicate,take,take-nth,take-while, andtree-seq

(map #(println %) [1 2 3])

;; 有很多方法可以强制LazySeq对它里面的方法进行调用。比如从序列里面获取一个元素的方法first,second,nth 以及last 都能达到这个效果。序列里面的方法是按顺序调用的， 所以你如果要获取最后一个元素， 那么整个LazySeq里面的方法都会被调用。


;;                                                                   结果要缓存                             只要求方法被执行，不需要缓存
;; 操作单个序列 	                                   doall函数 	                      dorun函数
;; 利用list comprehension语法来操作多个序列      N/A 	                                 doseq宏 

(dorun (map #(println %) [1 2 3])) ;;dorun内部使用map又创建了另外一个序列, 效率比doseq差
(doseq [i [1 2 3]] (println i)) ;; 代码更加易懂。 同时代码效率也更高
;; 如果一个方法会返回一个LazySeq并且在它的方法被调用的时候还会有副作用，那么大多数情况下我们应该使用doall 来调用并且返回它的结果。这样避免产生多次副作用 

(doseq [item [1 2 3]] (println item)) ;; -> nil
(dorun (map #(println %) [1 2 3])) ;; -> nil
(doall (map #(do (println %) %) [1 2 3])) ; -> (1 2 3) 

;; 实现无线序列
(defn f
  "square the argument and divide by 2"
  [x]
  (println "calculating f of" x)
  (/ (* x x) 2.0))
 
; 实现一个序列，这个序列从0开始，直到无穷 f[n] = n * n / 2.0
;; 这个数组的头会被绑定到f-seq这个变量上，所有被计算过的值都会被缓存
(def f-seq (map f (iterate inc 0)))

;; 强制计算f[0]， 并缓存
(println "first is" (first f-seq)) ; -> 0.0
 
;;强制计算前面3个值， 但只计算f[1], f[2]
(doall (take 3 f-seq)) 
 
;; 从缓存拿到f[2] 
(println (nth f-seq 2)) 

;; 不做本地绑定, 所需要的内存比较少， 但是如果同一个元素被请求多次， 那么它的效率会低一点。
(defn f-seq-fn [] (map f (iterate inc 0)))
(println (first (f-seq-fn))) ; evaluates (f 0), but doesn't cache result
(println (nth (f-seq-fn) 2)) ; evaluates (f 0), (f 1) and (f 2) 

;; 把LazySeq直接传给函数
(defn consumer [seq]
  ; Since seq is a local binding, the evaluated items in it
  ; are cached while in this function and then garbage collected.
  (println (first seq)) ; evaluates (f 0)
  (println (nth seq 2))) ; evaluates (f 1) and (f 2)
 
(consumer (map f (iterate inc 0)))


