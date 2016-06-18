(ns clojure-web.urlhandlers
  (:use [compojure.core :only [GET POST PUT DELETE defroutes]]
        [compojure.route :only [not-found]] 
        [ring.util.response :only [response]]
        clojure-web.courses))

(init-courses!)

(defroutes app-routes

  (GET "/" request (response {:template "index.html",
                              :model {:id "u-20151123-9091",
                                      :name "Klose"
                                      :lessons (get-courses)}}))

  (POST "/courses" [] (fn [request]
                        (let [params (:params request)]
                          (create-course! {:id (str "c-" (System/currentTimeMillis)),
                                           :name (:name params),
                                           :price 8.8,
                                           :online true,
                                           :days 7})
                          (response (str "You have created course: " (:name params))))))

  (GET "/rest/courses" [] (response { :courses (get-courses) }))

  (POST "/rest/courses" [] (fn [request]
                             (let [c (:body request)
                                   id (str "c-" (System/currentTimeMillis))]
                               (create-course! (assoc c :id id, :online true,))
                               (response (get-course id)))))

  (GET "/rest/courses/:id" [id] (response (get-course id)))

  (POST "/rest/courses/delete" [] (fn [request]
                                     (let [params (:params request)]
                                       (delete-course! (:id params))
                                       (response {:id params}))))

  (not-found "<h1>page not found!</h1>"))
