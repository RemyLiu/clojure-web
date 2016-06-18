;; 用来协调对于一个或者多个binding的并发修改的。这个协调机制是利用Software Transactional Memory (STM)来实现的
;; 在一个STM事务里面做的修改只有在事务提交之后别的线程才能看到。这实现了ACID里面的A和I。Validation函数是的对Ref的修改与跟它相关的其它的值是一致的, 也就实现了C。要想你的代码在一个事务里面执行， 那么要把你的代码包在宏dosync 的体内。当在一个事务里面对值进行修改，被改的其实是一个私有的、线程内的、直到事务提交才会被别的线程看到的一快内存。
;; 如果到事务结束的时候也没有异常抛出的话， 那么这个事务会顺利的提交， 在事务里面所作的改变也就可以被别的线程看到了。
;; 如果在事务里面有一个异常抛出，包括validation函数抛出的异常，那么这个事务会被回滚，事务里面对值做的修改也就会撤销。
;; 如果在一个事务里面，我们要对一个Ref进行修改，但是发现从我们的事务开始之后，已经有别的线程对这个Ref做了改动， 那么当前事务里面的改动会被撤销，然后从dosync的开头重试。那到底什么时候会检测到冲突， 什么时候会进行重试， 这个是没有保证的， 唯一保证的是clojure为检测到冲突，并且会进行重试。
;; 要在事务里面执行的代码一定要是没有副作用的，这一点非常重要，因为前面提到的，事务可能会跟别的事务事务冲突，然后重试， 如果有副作用的话，那么出来的结果就不对了。不过要执行有副作用的代码也是可能的， 可以把这个方法调用包装给Agent, 然后这个方法会被hold住直到事务成功提交，然后执行一次。如果事务失败那么就不会执行。 

                                        ; Assume the only account data that can change is its balance.
(defstruct account-struct :id :owner :balance-ref)

                                        ; We need to be able to add and delete accounts to and from a map.
                                        ; We want it to be sorted so we can easily
                                        ; find the highest account number
                                        ; for the purpose of assigning the next one. 
(def account-map-ref (ref (sorted-map))) ;; 使用ref函数来定义一个Refs引用

(defn open-account
  "creates a new account, stores it in the account map and returns it"
  [owner]
  (dosync ; required because a Ref is being changed ;;必须在dosync中修改一个Ref引用, 执行完这段语句后，改变的Ref引用对其他线程可见 
   (let [account-map @account-map-ref ;; 使用@宏来获取一个Refs引用
         last-entry (last account-map)
                                        ; The id for the new account is one higher than the last one.
         id (if last-entry (inc (key last-entry)) 1)
                                        ; Create the new account with a zero starting balance.
         account (struct account-struct id owner (ref 0))] 
                                        ; Add the new account to the map of accounts.
     (alter account-map-ref assoc id account) ;;使用alter来改变一个Ref引用
                                        ; Return the account that was just created.
     account))) 

(defn deposit [account amount]
  "adds money to an account; can be a negative amount"
  (dosync ; required because a Ref is being changed
   (Thread/sleep 50) ; simulate a long-running operation
   (let [owner (account :owner)
         balance-ref (account :balance-ref)
         type (if (pos? amount) "deposit" "withdraw")
         direction (if (pos? amount) "to" "from")
         abs-amount (Math/abs amount)]
     (if (>= (+ @balance-ref amount) 0) ; sufficient balance?
       (do
         (alter balance-ref + amount)
         (println (str type "ing") abs-amount direction owner))
       (throw (IllegalArgumentException.
               (str "insufficient balance for " owner
                    " to withdraw " abs-amount)))))))

(defn withdraw
  "removes money from an account"
  [account amount]
                                        ; A withdrawal is like a negative deposit.
  (deposit account (- amount)))

(defn transfer [from-account to-account amount]
  (dosync
   (println "transferring" amount
            "from" (from-account :owner)
            "to" (to-account :owner))
   (withdraw from-account amount)
   (deposit to-account amount))) 

(defn- report-1 ; a private function
  "prints information about a single account"
  [account]
                                        ; This assumes it is being called from within
                                        ; the transaction started in report.
  (let [balance-ref (account :balance-ref)]
    (println "balance for" (account :owner) "is" @balance-ref)))

(defn report
  "prints information about any number of accounts"
  [& accounts]
  (dosync (doseq [account accounts] (report-1 account))))

                                        ; Set a default uncaught exception handler
                                        ; to handle exceptions not caught in other threads.
(Thread/setDefaultUncaughtExceptionHandler
 (proxy [Thread$UncaughtExceptionHandler] []
   (uncaughtException [thread throwable]
                                        ; Just print the message in the exception.
     (println (.. throwable .getCause .getMessage)))))

(let [a1 (open-account "Mark")
      a2 (open-account "Tami")
      thread (Thread. #(transfer a1 a2 50))]
  (try
    (deposit a1 100)
    (deposit a2 200)
    
                                        ; There are sufficient funds in Mark's account at this point
                                        ; to transfer $50 to Tami's account.
    (.start thread) ; will sleep in deposit function twice!
    
                                        ; Unfortunately, due to the time it takes to complete the transfer
                                        ; (simulated with sleep calls), the next call will complete first.
    (withdraw a1 75)
    
                                        ; Now there are insufficient funds in Mark's account
                                        ; to complete the transfer.
    
    (.join thread) ; wait for thread to finish
    (report a1 a2)
    (catch IllegalArgumentException e
      (println (.getMessage e) "in main thread"))))

;; Note the use of the :validator directive when creating the Ref
;; to assign a validation function which is integer? in this case.
(def my-ref (ref 0 :validator integer?))

(try
  (dosync
   (ref-set my-ref 1) ; works

   ;; The next line doesn't work, so the transaction is rolled back
   ;; and the previous change isn't committed.
   (ref-set my-ref "foo"))
  (catch IllegalStateException e
    ;; do nothing
    ))

(println "my-ref =" @my-ref) ; due to validation failure -> 0
