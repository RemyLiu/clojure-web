;; Var: 是一种可以有一个被所有线程共享的root binding并且每个线程线程还能有自己线程本地(thread-local)的值的一种引用类型 
;; def: 创建一个Var并且给它一个root binding 
;; set!和binding: 可以创建一个已经存在的Var的线程本地binding 


(def v 1)
(defn change-it []
  (println "2) v =" v) ; -> 1  
  (set! v 2) ; changes root value
  (println "3) v =" v) ; -> 2
  (binding [v 3] ; binds a thread-local value
    (println "4) v =" v) ; -> 3
    (set! v 4) ; changes thread-local value
    (println "5) v =" v)) ; -> 4
  (println "6) v =" v)) ; thread-local value is gone now -> 2

(println "1) v =" v) ; -> 1

(let [thread (Thread. #(change-it))]
  (.start thread)
  (.join thread)) ; wait for thread to finish

(println "7) v =" v) ; -> 2 

;; 不鼓励使用 Vars， 因为线程之间对于同一个Var的修改没有做很好的协调，比如线程A在使用一个Var的root值，然后才发现，在它使用这个值的时候，已经有一个线程B在修改这个值了。 
