(ns server
  (:require [ring.util.response :refer [file-response]]
            [compojure.core :refer [defroutes GET PUT POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]))

(defroutes routes
  (GET "/" [] (file-response "public/index.html" {:root "resources"}))
  (route/files "/public" {:root "resources/public"})
  (route/not-found "Page not found"))

(def app
  (-> routes))
