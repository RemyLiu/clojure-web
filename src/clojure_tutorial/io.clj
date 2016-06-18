(binding [*out* (java.io.FileWriter. "my.log")]
  (println "This goes to the file my.log.")
  (flush))

;;pr 与prn 是和print 与println 想对应的一对函数, 但是他们输出的形式可以被 Clojure reader去读取。
(let [obj1 "foo"
      obj2 {:letter \a :number (Math/PI)}] ; a map
  (println "Output from print:")
  (print obj1 obj2)
  
  (println "Output from println:")
  (println obj1 obj2)
  
  (println "Output from pr:")
  (pr obj1 obj2)
  
  (println "Output from prn:")
  (prn obj1 obj2))

(println "foo" 19) ; -> foo 19
(println (str "foo" 19)) ; -> foo19

;; print-str,println-str,pr-str 以及prn-str 函数print,println,pr 跟prn 类似, 只是它们返回一个字符串，而不是把他们打印出来。

;; printf 函数和print 类似。但是它接受一个format字符串。format 函数和printf, 类似，只是它是返回一个字符串而不是打印出来。

;;with-out-str宏把它的方法体里面的所有输出汇总到一个字符串里面并且返回。

;; with-open 可以自动关闭所关联的连接（.close)方法， 这对于那种像文件啊，数据库连接啊，比较有用。

;; line-seq 接受一个java.io.BufferedReader 参数，并且返回一个LazySeq, 这个LazySeq包含所有的一行一行由BufferedReader读出的文本。

(defn print-if-contains [line word]
  (when (.contains line word) (println line)))
 
(let [file "story.txt"
      word "fur"]
  ; with-open will close the reader after
  ; evaluating all the expressions in its body.
  (with-open [rdr (reader file)]
    (doseq [line (line-seq rdr)] (print-if-contains line word))))

