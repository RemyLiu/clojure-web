;; 

(count [19 "yellow" true]) ;; 3
(reverse [2 4 7]) ; -> (7 4 2) 
                                        ; The next line uses an anonymous function that adds 3 to its argument.
(map #(+ % 3) [2 4 7]) ; -> (5 7 10)
(map + [2 4 7] [5 6] [1 2 3 4]) ; adds corresponding items -> (8 12) 

(apply + [2 4 7]); -> 13 

(def stooges ["Moe" "Larry" "Curly" "Shemp"])
(first stooges) ; -> "Moe"
(second stooges) ; -> "Larry"
(last stooges) ; -> "Shemp"
(nth stooges 2) ; indexes start at 0 -> "Curly" 

(next stooges) ; -> ("Larry" "Curly" "Shemp")
(butlast stooges) ; -> ("Moe" "Larry" "Curly")
(drop-last 2 stooges) ; -> ("Moe" "Larry")
                                        ; Get names containing more than three characters.
(filter #(> (count %) 3) stooges) ; -> ("Larry" "Curly" "Shemp")
(nthnext stooges 2) ; -> ("Curly" "Shemp") 

(every? #(instance? String %) stooges) ; -> true
(not-every? #(instance? String %) stooges) ; -> false
(some #(instance? Number %) stooges) ; -> nil
(not-any? #(instance? Number %) stooges) ; -> true 

;;list 

(def stooges (list "Moe" "Larry" "Curly"))
(def stooges (quote ("Moe" "Larry" "Curly")))
(def stooges '("Moe" "Larry" "Curly")) 

(some #(= % "Moe") stooges) ; -> true
(some #(= % "Mark") stooges) ; -> nil
                                        ; Another approach is to create a set from the list
                                        ; and then use the contains? function on the set as follows.
(contains? (set stooges) "Moe") ; -> true 

(def more-stooges (conj stooges "Shemp")) ;;-> ("Shemp" "Moe" "Larry" "Curly")
(def less-stooges (remove #(= % "Curly") more-stooges)) ;; ->("Shemp" "Moe" "Larry") 

(def kids-of-mike '("Greg" "Peter" "Bobby"))
(def kids-of-carol '("Marcia" "Jan" "Cindy"))
(def brady-bunch (into kids-of-mike kids-of-carol))
(println brady-bunch) ; -> (Cindy Jan Marcia Greg Peter Bobby)

;; vectors 
(def stooges (vector "Moe" "Larry" "Curly"))
(def stooges ["Moe" "Larry" "Curly"]) 

(get stooges 1 "unknown") ;; "Larry"
(get stooges 3 "unknown") ;; "unknown"

;;assoc 可以对 vectors 和 maps进行操作。 当用在 vector上的时候, 它会从给定的vector创建一个新的vector, 而指定的那个索引所对应的元素被替换掉。如果指定的这个索引等于vector里面元素的数目，那么我们会把这个元素加到新vector的最后面去；如果指定的索引比vector的大小要大，那么一个IndexOutOfBoundsException 异常会被抛出来。
(def replaced-stooges (assoc stooges 2 "Shemp")) ; -> ["Moe" "Larry" "Shemp"] 

;; subvec 获取一个给定vector的子vector。它接受三个参数，一个vectore, 一个起始索引以及一个可选的结束索引。如果结束索引没有指定，那么默认的结束索引就是vector的大小 
(subvec stooges 0 2) ;; ["Moe" "Larry"] 
(subvec stooges 1) ;;  ["Larry" "Curry"] 

(pop stooges)  ;; ["Moe" "Larry"] 
stooges ;; ["Moe" "Larry" "Curly"]
(peek stooges) ;; "Curly"

;;Set #{"A" "B" "C"} hashed-set

(def stooges (hash-set "Moe" "Larry" "Curly")) ; not sorted
(def stooges #{"Moe" "Larry" "Curly"}) ; same as previous
(def stooges (sorted-set "Moe" "Larry" "Curly"))

(contains? stooges "Moe") ; -> true
(contains? stooges "Mark") ; -> false

(stooges "Moe") ; -> "Moe"
(stooges "Mark") ; -> nil

(println (if (stooges "person") "stooge" "regular person"))
;;regular person

(def more-stooges (conj stooges "Shemp")) ; -> #{"Moe" "Larry" "Curly" "Shemp"}
(def less-stooges (disj more-stooges "Curly")) ; -> #{"Moe" "Larry" "Shemp"}

;;Map {:1 :A, :2 : B, :3 :C}

(def popsicle-map
  (hash-map :red :cherry, :green :apple, :purple :grape))
(def popsicle-map
  {:red :cherry, :green :apple, :purple :grape}) ; same as previous
(def popsicle-map
  (sorted-map :red :cherry, :green :apple, :purple :grape))

(contains? popsicle-map :green) ; -> true
(keys popsicle-map) ; -> (:red :green :purple)
(vals popsicle-map) ; -> (:cherry :apple :grape) 

(assoc popsicle-map :green :lime :blue :blueberry) ;; {:blue :blueberry, :green :lime, :purple :grape, :red :cherry} 
(dissoc popsicle-map :green :blue) ;;  {:purple :grape, :red :cherry} 

;;遍历popsicle-map 里面的所有元素，把key bind到color， 把value bind到flavor。 name函数返回一个keyword的字符串名字。
(doseq [[color flavor] popsicle-map]
  (println (str "The flavor of " (name color)
                " popsicles is " (name flavor) ".")))

;; The flavor of red popsicles is cherry.
;; The flavor of green popsicles is apple.
;; The flavor of purple popsicles is grape.

;;select-keys 函数接收一个map对象，以及一个key的集合的参数，它返回这个集合里面key在那个集合里面的一个子map。看例子
(select-keys popsicle-map [:red :green :blue])

;;{:red :cherry, :green :apple}

(def person {
             :name "Mark Volkmann"
             :address {
                       :street "644 Glen Summit"
                       :city "St. Charles"
                       :state "Missouri"
                       :zip 63304}
             :employer {
                        :name "Object Computing, Inc."
                        :address {
                                  :street "12140 Woodcrest Executive Drive, Suite 250"
                                  :city "Creve Coeur"
                                  :state "Missouri"
                                  :zip 63141}}})

;; get-in 函数、宏-> 以及函数reduce 都可以用来获得内嵌的key 
(get-in person [:employer :address :city]) ;; "Creve Coeur" 
;; 宏-> 我们也称为 “thread” 宏, 它本质上是调用一系列的函数，前一个函数的返回值作为后一个函数的参数. 
;; (f1 (f2 (f3 x)))
;; (-> x f3 f2 f1) 
(-> person :employer :address :city) ;; "Creve Coeur"  

;;reduce 函数接收一个需要两个参数的函数, 一个可选的value以及一个集合。
;;它会以value以及集合的第一个元素作为参数来调用给定的函数（如果指定了value的话）， 
;; 要么以集合的第一个元素以及第二个元素为参数来调用给定的函数（如果没有指定value的话)。
;; 接着就以这个返回值以及集合里面的下一个元素为参数来调用给定的函数
(reduce get person [:employer :address :city]) ;; "Creve Coeur" 

;; assoc-in 函数可以用来修改一个内嵌的key的值
(assoc-in person [:employer :address :city] "Clayton") 
;; {:name "Mark Volkmann", :address {:street "644 Glen Summit", :city "St. Charles", :state "Missouri", :zip 63304}, :employer {:name "Object Computing, Inc.", :address {:street "12140 Woodcrest Executive Drive, Suite 250", :city "Clayton", :state "Missouri", :zip 63141}}} 

;; update-in 函数也是用来更新给定的内嵌的key对应的值，只是这个新值是通过一个给定的函数来计算出来。 
(update-in person [:employer :address :zip] str "-1234") 
;;  {:name "Mark Volkmann", :address {:street "644 Glen Summit", :city "St. Charles", :state "Missouri", :zip 63304}, :employer {:name "Object Computing, Inc.", :address {:street "12140 Woodcrest Executive Drive, Suite 250", :city "Creve Coeur", :state "Missouri", :zip "63141-1234"}}} 
 
;; structed map 
;; tructMap和普通的map类似，它的作用其实是用来模拟java里面的javabean， 所以它比普通的map的优点就是，它把一些常用的字段抽象到一个map里面去，这样你就不用一遍一遍的重复了。他会帮你生成合适的equals 和hashCode 方法。并且它还提供方式让你可以创建比普通map里面的hash查找要快的字段访问方法(javabean里面的getXXX方法)。

;; create-struct 方法
(def vehicle-struct (create-struct :make :model :year :color)) 
;; defstruct宏
(defstruct vehicle-struct :make :model :year :color) 

;; struct 实例化StructMap的一个对象，相当于java里面的new关键字. 你提供给struct的参数的顺序必须和你定义的时候提供的keyword的顺序一致，后面的参数可以忽略， 如果忽略，那么对应key的值就是nil。 
(def vehicle (struct vehicle-struct "Toyota" "Prius" 2009)) 
;; {:make "Toyota", :model "Prius", :year 2009, :color nil}

;; accessor 函数可以创建一个类似java里面的getXXX的方法，
; Note the use of def instead of defn because accessor returns
; a function that is then bound to "make".
(def make (accessor vehicle-struct :make))
(make vehicle) ; -> "Toyota"
(vehicle :make) ; same but slower
(:make vehicle) ; same but slower


