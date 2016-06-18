(defproject clojure-web "0.1.0-SNAPSHOT"
  :description "sample of database-backed Clojure web application"
  :url "https://github.com/klose911/clojure-web"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"sonatype-oss-public" "https://oss.sonatype.org/content/groups/public/"}
  :dependencies [
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [korma "0.3.0"]
                 [selmer "0.7.2"]
                 [ring "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [compojure "1.2.1"]]
  :main clojure-web.core)
  ;; :aot [clojure-web.web]
  ;; :plugins [[lein-ring "0.8.13"]]
  ;; :ring {:handler clojure-web.web/app
  ;;        :auto-reload? true
  ;;        :auto-refresh? true
  ;;        }
