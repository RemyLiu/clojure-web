(ns clojure-web.core)

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(println (foo "Clojure:"))


(defn -main [& args]
  (println "hello world"))
