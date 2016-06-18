;; Agents 是用把一些事情放到另外一个线程来做,一般来说不需要事务控制的。它们对于修改一个单个对象的值(也就是Agent的值)来说很方便。
;; 这个值是通过在另外的一个thread上面运行一个“action”来修改的。一个action是一个函数， 这个函数接受Agent的当前值以及一些其它参数。 
;; 在任意一个时间点一个Agent实例上面只能运行一个action. 

;; agent 函数可以建立一个新的Agent. 
;; send 函数把一个 action 分配给一个 Agent， 并且马上返回而不做任何等待。 这个action会在另外一个线程(一般是由一个线程池提供的)上面单独运行。 当这个action运行结束之后，返回值会被设置给这个Agent. 
;; send 使用一个 "固定大小的" 线程池 (java.util.concurrent.Executors里面的newFixedThreadPool ) ， 线程的个数是机器的处理器的个数加2。如果所有的线程都被占用，那么你如果要运行新的action， 那你就要等了。send-off 使用的是 "cached thread pool" (java.util.concurrent.Executors里面的?newCachedThreadPool) ， 这个线程池里面的线程的个数是按照需要来分配的。
;; 如果send 或者send-off 函数是在一个事务里面被调用的。 那么这个action直到线程提交的时候才会被发送给另外一个线程去执行。 
;; 在action里面， 相关联的那个agent可以通过symbol：*agent*得到。 
;; await 以一个或者多个Agent作为参数， 并且block住当前的线程，直到当前线程分派给这些Agent的action都执行完了。await-for 函数是类似的, 但是它接受一个超时时间作为它的第一个参数， 如果在超时之前事情都做完了， 那么返回一个非nil的值， 否则返回一个非nil的值，而且当前线程也就不再被block了。await 和await-for 函数不能在事务里面调用。
;; 如果一个action执行的时候抛出一个异常了，那么你要dereference这个Agent的话也会抛出异常的。在action里面抛出的所有的异常可以通过agent-errors 函数获取。 
;; clear-agent-errors 函数可以清除一个指定Agent上面的所有异常。
;; shutdown-agents 函数等待所有发送给agents的action都执行完毕。然后它停止线程池里面所有的线程。在这之后你就不能发送新的action了。我们一定要调用shutdown-agents 以让JVM 可以正常退出，因为Agent使用的这些线程不是守护线程， 如果你不显式关闭的话，JVM是不会退出的。


(def my-watcher (agent {}))
 
(defn my-watcher-action [current-value reference]
  (let [change-count-map current-value
        old-count (change-count-map reference)
        new-count (if old-count (inc old-count) 1)]
  ;; Return an updated map of change counts
  ;; that will become the new value of the Agent.
  (assoc change-count-map reference new-count)))
 
(def my-var "v1")
(def my-ref (ref "r1"))
(def my-atom (atom "a1"))
 
(defn w-1 [key id old new ] 
  (println "w-1" "key" key "id" id "old" old "new" new)) 
 
(add-watch (var my-var) "watch-var" w-1)
(add-watch my-ref "watch-ref"  w-1)
(add-watch my-atom "watch-atom" w-1)
 
;; Change the root binding of the Var in two ways.
(def my-var "v2")
(alter-var-root (var my-var) (fn [curr-val] "v3"))
 
;; Change the Ref in two ways.
(dosync
  ;; The next line only changes the in-transaction value
  ;; so the watcher isn't notified.
  (ref-set my-ref "r2")
  ;; When the transaction commits, the watcher is
  ;; notified of one change this Ref ... the last one.
  (ref-set my-ref "r3"))
(dosync
  (alter my-ref (fn [_] "r4"))) ; And now one more.
 
;; Change the Atom in two ways.
(reset! my-atom "a2")
(compare-and-set! my-atom @my-atom "a3")
 
;; Wait for all the actions sent to the watcher Agent to complete.
(await my-watcher)
 
;; Output the number of changes to
;; each reference object that was watched.
(let [change-count-map @my-watcher]
  (println "my-var changes =" (change-count-map (var my-var))) ; -> 2
  (println "my-ref changes =" (change-count-map my-ref)) ; -> 2
  (println "my-atom changes =" (change-count-map my-atom))) ; -> 2
 
(shutdown-agents)
