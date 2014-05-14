(ns app.core
  (:require [om.core :as om :include-macros true]
            [clojure.browser.repl :as repl]
            [clojure.string :as string]
            [cljs.reader :as reader]
            [goog.events :as events]
            [goog.cssom :as cssom]
            [goog.dom :as gdom]
            [sablono.core :as html :refer-macros [html]]
            [garden.core :refer [css]]
            [cljs.core.async :refer [put! chan <! timeout]])
  (:import goog.net.EventType
           goog.History
           goog.history.EventType
           [goog.net XhrIo]
           goog.events.EventType))

(enable-console-print!)

(defn widget [data]
  (reify
    om/IRender
    (render [this]
      (html [:div "Hello world"]))))
    
(om/root widget {}
  {:target (.getElementById js/document "app")})
  
