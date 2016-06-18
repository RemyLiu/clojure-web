(ns clojure-web.templ)

;; using selmer templating system
(use 'selmer.parser)

(selmer.parser/cache-off!)

(selmer.parser/set-resource-path! (clojure.java.io/resource "templates"))

(render-file "test.html" {:title "Klose Template",
                          :name "Klose",
                          :now (new java.util.Date)})
