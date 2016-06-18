(ns clojure-web.courses
  (:use korma.db
        korma.core))

(defdb korma-db (mysql {:db "test",
                        :host "192.168.2.160",
                        :port 3306,
                        :user "root",
                        :password "mysql"}))

(declare courses)

(defentity courses)

(defn get-course [id]
  (let [cs (select courses
                   (where {:id id}))]
    (if (empty? cs)
      nil
      (first cs))))

(defn get-courses []
  (select courses
          (where {:online true})
          (order :name :asc)))

(defn create-course! [c]
  (println "create course:" c)
  (insert courses
          (values c)))

(defn delete-course! [id]
  (println "delete course:" id)
  (delete courses
          (where {:id id}))) 

(defn init-courses! []
  (if (empty? (get-courses))
    (let [cs [{ :id "c-101", :name "Clojure", :price 19.9, :online true, :days 20 },
              { :id "c-102", :name "Java",    :price 9.9,  :online true, :days 15 },
              { :id "c-103", :name "Python",  :price 15.0, :online true, :days 18 }]]
      (println "init courses...")
      (dorun
       (map create-course! cs)))))
