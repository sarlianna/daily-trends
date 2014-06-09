(ns core
  (:require [ring.util.response :refer [file-response]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [compojure.core :refer [defroutes GET PUT POST DELETE]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [datomic.api :as d]))

(def uri "datomic:free://localhost:4334/trends")
(def conn (d/connect uri))

(defn generate-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/edn"}
   :body (pr-str data)})

(defn tags []
  (let [db (d/db conn)
        tags
        (vec (map #(d/touch (d/entity db (first %)))
                  (d/q '[:find ?tag
                               :where
                               [?tag :tag/title]]
                             db)))]
    (generate-response tags)))

(defn create-tag [params]
  (let [title (:tag/title params)]
    (d/transact conn [{:db/id #db/id[:db.part/user] :tag/title title}])
    (generate-response {:status :ok})))

; currently there's a bug where this updates the first tag in the collection
; and not the tag specified by title
(defn update-tag [title params]
  (let [db    (d/db conn)
        new-title (:tag/title params)
        eid   (ffirst
                (d/q '[:find ?tag
                             :where
                             [?tag :tag/title ?title]]
                   db))]
    (d/transact conn [{:db/id eid :tag/title new-title}])
    (generate-response {:status :ok})))

(defn days []
  (let [db (d/db conn)
       days
        (vec (map #(d/touch (d/entity db (first %)))
                  (d/q '[:find ?occurance
                               :where
                               [?occurance :occurance/id]]
                             db)))]
    (generate-response days)))

(defn single-day [id]
  (let [db (d/db conn)
        day
        (#(d/touch (d/entity db (ffirst %)))
          (d/q ' [:find ?occurance
                        :where
                        [?occurance :occurance/id ?id]]
                        db))]
    (generate-response day)))

(defn create-day [params]
  (let [db (d/db conn)
        tag-title (:day/tag params)
        tag (ffirst
              (d/q '[:find ?tag
                           :where
                           [?tag :tag/title ?tag-title]] db))
        day (if-not (nil? (:day/day params)) (:day/day params) (new java.util.Date))
        degree (if-not (nil? (:day/degree params)) (:day/degree params) :occurance.degreetype/present)
        positivity (if-not (nil? (:day/positivity params)) (:day/positivity params) :occurance.postype/neutral)]
    (d/transact conn [{:db/id #db/id[:db.part/user]
                             :occurance/id  (str (java.util.UUID/randomUUID))
                             :occurance/tag tag
                             :occurance/day day
                             :occurance/degree degree
                             :occurance/positivity positivity}])
    (generate-response {:status :ok})))

(defn update-day [id params]
  (let [db    (d/db conn)
        tag-title (:day/tag params)
        tag (ffirst
              (d/q '[:find ?tag
                           :where
                           [?tag :tag/title ?tag-title]] db))
        day (:day/day params)
        degree (:day/degree params)
        positivity (:day/positivity params)
        eid   (ffirst
                (d/q '[:find ?occurance
                             :where
                             [?occurance :occurance/id ?id]]
                   db id))]
    (d/transact conn [{:db/id eid
                             :occurance/tag tag
                             :occurance/day day
                             :occurance/degree degree
                             :occurance/positivity positivity}])
    (generate-response {:status :ok})))

(defn delete-day [id params]
  (let [db    (d/db conn)
        eid   (ffirst
                (d/q '[:find ?occurance
                             :where
                             [?occurance :occurance/id ?id]]
                   db))]
    (d/transact conn [[:db.fn/retractEntity eid]])
    (generate-response {:status :ok})))

(defroutes routes

  (GET "/" [] (file-response "public/index.html" {:root "resources"}))

  (GET "/tags" [] (tags))
  (POST "/tags"
    {params :params edn-params :edn-params}
    (create-tag params))
  (PUT "/tags/:title"
    {{title :title} :params params :params edn-params :edn-params}
    (update-tag title params))

  (GET "/days" [] (days))
  (GET "/days/:id" [id] (single-day id))
  (POST "/days"
    {params :params edn-params :edn-params}
    (create-day params))
  (PUT "/days/:id"
    {{id :id} :params params :params edn-params :edn-params}
    (update-day id params))
  (DELETE "/days/:id"
    {{id :id} :params params :params edn-params :edn-params}
    (delete-day id params))

  (route/files "/public" {:root "resources/public"})
  (route/not-found "Page not found"))

(def app
  (-> routes
      wrap-edn-params))
