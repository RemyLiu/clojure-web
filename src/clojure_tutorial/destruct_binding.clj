;;解构可以用在一个函数或者宏的参数里面来把一个集合里面的一个或者几个元素抽取到一些本地binding里面去。
;; 它可以用在由let special form或者binding宏所创建的binding里面。
(defn approach1 [numbers]
  (let [n1 (first numbers)
        n3 (nth numbers 2)]
    (+ n1 n3)))

                                        ; Note the underscore used to represent the
                                        ; second item in the collection which isn't used.
(defn approach2 [[n1 _ n3]] (+ n1 n3))

(approach1 [4 5 6 7]) ; -> 10
(approach2 [4 5 6 7]) ; -> 10

;; &符合可以在解构里面用来获取集合里面剩下的元素 
(defn name-summary [[name1 name2 & others]]
  (println (str name1 ", " name2) "and" (count others) "others"))

(name-summary ["Moe" "Larry" "Curly" "Shemp"]) ;; -> Moe, Larry and 2 others 

;; :as 关键字可以用来获取对于整个被解构的集合的访问
(defn first-and-third-percentage [[n1 _ n3 :as coll]]
  (/ (+ n1 n3) (apply + coll)))
(first-and-third-percentage [4 5 6 7]) ; ratio reduced from 10/22 -> 5/11

;; 使用和map里面key的名字一样的本地变量来对map进行解构，比如{june :june july :july august :august :as all}. 
;; 这个可以使用:keys来简化。比如,{:keys [june july august] :as all}.
(defn summer-sales-percentage
  ;; The keywords below indicate the keys whose values
  ;; should be extracted by destructuring.
  ;; The non-keywords are the local bindings
  ;; into which the values are placed.
  [{june :june july :july august :august :as all}] ;;
  (let [summer-sales (+ june july august)
        all-sales (apply + (vals all))]
    (/ summer-sales all-sales)))

(def sales {
            :january   100 :february 200 :march      0 :april    300
            :may       200 :june     100 :july     400 :august   500
            :september 200  :october  300 :november 400 :december 600})

(summer-sales-percentage sales) ; ratio reduced from 1000/3300 -> 10/33
