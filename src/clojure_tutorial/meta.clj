;; 元数据是附加到一个符号或者集合的一些数据
;;它们和符号或者集合的逻辑数据没有直接的关系。两个逻辑上一样的方法可以有不同的元数据。
(defstruct card-struct :rank :suit)
 
(def card1 (struct card-struct :king :club))
(def card2 (struct card-struct :king :club))
 
(println (== card1 card2)) ; same identity? -> false
(println (= card1 card2)) ; same value? -> true
 
(def card2 #^{:bent true} card2) ; adds metadata at read-time
(def card2 (with-meta card2 {:bent true})) ; adds metadata at run-time
(println (meta card1)) ; -> nil
(println (meta card2)) ; -> {:bent true}
(println (= card1 card2)) ; still same value despite metadata diff. -> true
