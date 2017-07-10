(ns war.events
  (:require [re-frame.core :as re-frame]
            [war.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/reg-event-db
  :update-decks
  (fn [db [_ new-decks]]
    (assoc db :decks new-decks)))
