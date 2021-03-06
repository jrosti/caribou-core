(ns caribou.field.id
  (:require [caribou.db :as db]
            [caribou.validation :as validation]
            [caribou.field :as field]))

(defrecord IdField [row env]
  field/Field
  (table-additions [this field] [[(keyword field) "SERIAL" "PRIMARY KEY"]])
  (subfield-names [this field] [])
  (setup-field [this spec]
    (let [model (db/find-model (:model-id row) (field/models))]
      (db/create-index (:slug model) (:slug row))))
  (rename-model [this old-slug new-slug]
    (field/rename-model-index old-slug new-slug (-> this :row :slug)))
  (rename-field [this old-slug new-slug]
    (field/rename-index this old-slug new-slug))
  (cleanup-field [this]
    (let [model (db/find-model (:model-id row) (field/models))]
      (db/drop-index (:slug model) (:slug row))))
  (target-for [this] nil)
  (update-values [this content values original] values)
  (post-update [this content opts] content)
  (pre-destroy [this content] content)
  (join-fields [this prefix opts] [])
  (join-conditions [this prefix opts] [])
  (build-where
    [this prefix opts]
    (field/integer-where this prefix opts))
  (natural-orderings [this prefix opts])
  (build-order [this prefix opts]
    (field/pure-order this prefix opts))
  (field-generator [this generators]
    generators)
  (fuse-field [this prefix archetype skein opts]
    (field/pure-fusion this prefix archetype skein opts))
  (localized? [this] false)
  (propagate-order [this id orderings])
  (models-involved [this opts all]
    (field/id-models-involved this opts all))
  (field-from [this content opts] (content (keyword (:slug row))))
  (render [this content opts] content)
  (validate [this opts] (validation/for-type this opts integer? "id")))

(defn constructor
  [row]
  (IdField. row {}))
