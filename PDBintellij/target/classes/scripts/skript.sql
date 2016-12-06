DROP TABLE racks CASCADE CONSTRAINTS;
DROP TABLE rack_definitions CASCADE CONSTRAINTS;
DROP TABLE goods CASCADE CONSTRAINTS;
DROP TABLE rack_goods CASCADE CONSTRAINTS;


-- místost stojanů - mapa místosti - prostor, kde jsou umístěny stojany
CREATE TABLE racks (
	racks_id NUMBER(10) GENERATED BY DEFAULT ON NULL AS IDENTITY NOT NULL, -- PK
	racks_type NUMBER(10), -- foreign key to rack_definitions,
	racks_geometry SDO_GEOMETRY, -- tvar, který bude některý z rack_definitions + přidání umístění ve skladu
	racks_rotation NUMBER(1), -- bude moci nabývat čtyř hodnot - natočení po 90° - racks_geometry bude už ta otočená definice
	CONSTRAINT racks_pk PRIMARY KEY (racks_id) 
);


-- tabulka s různými typy stojanů - ty pak lze otáčet  aumisťovat do skladiště - naší mapy - racks
CREATE TABLE rack_definitions (
	rack_defs_id NUMBER(10) GENERATED BY DEFAULT ON NULL AS IDENTITY NOT NULL, -- PK
	rack_defs_name VARCHAR(32),
	rack_defs_capacity NUMBER(32,2), -- kolik se tam vleze zboží (objem)
	rack_defs_size_x NUMBER(10), -- minimal bounding box - kolik čtverečků zabírá stojan do šířky
	rack_defs_size_y NUMBER(10), -- minimal bounding box - kolik čtverečků zabírá stojan do výšky
	rack_defs_shape SDO_GEOMETRY, -- tvar stojanu - umistěný v začátku souřadného systému, 
								 -- opravdová hodnota umístění bude přiřazena až v tabulce stojany
	CONSTRAINT rack_definitions_pk PRIMARY KEY (rack_defs_id)
);



-- tabulka s různými typy zboží
CREATE TABLE goods (
	goods_id NUMBER(10) GENERATED BY DEFAULT ON NULL AS IDENTITY NOT NULL, -- PK
	goods_volume NUMBER(32,2), -- objem zboží, kolik prostoru zabere ve stojanu
	goods_name VARCHAR(32), 
	goods_photo ORDSYS.ORDImage,
	goods_photo_si ORDSYS.SI_StillImage,
	goods_photo_ac ORDSYS.SI_AverageColor,
	goods_photo_ch ORDSYS.SI_ColorHistogram,
	goods_photo_pc ORDSYS.SI_PositionalColor,
	goods_photo_tx ORDSYS.SI_Texture,
	goods_price NUMBER(32,2),
	CONSTRAINT goods_pk PRIMARY KEY (goods_id)
);


-- tabulka uchovávající informace o tom, že v určitém stojanu se nachází určité zboží,
-- a informaci o tom, kolik kusů tohoto konkrétního zboží se v daném stojanu nachází
-- temporální charakter, budeme chtít zaznamenávat, jak se zboží přesouvala z některých stojanů na jiné 
-- a také kdy se zboží exportovalo nebo inportovalo do skladu

CREATE TABLE rack_goods (
	racks_id NUMBER(10) NOT NULL, -- PK
	goods_id NUMBER(10) NOT NULL, -- PK
	rack_goods_count NUMBER(32),
	CONSTRAINT rack_goods_pk PRIMARY KEY (racks_id, goods_id)
) AS TRANSACTIONTIME;

ALTER TABLE rack_goods ADD CONSTRAINT fk_rack_goods_rack
  FOREIGN KEY (racks_id)
  REFERENCES racks(racks_id);

ALTER TABLE rack_goods ADD CONSTRAINT fk_rack_goods_goods
  FOREIGN KEY (goods_id)
  REFERENCES goods(goods_id);

ALTER TABLE racks ADD CONSTRAINT fk_racks_type_rack_defs
  FOREIGN KEY (racks_type)
  REFERENCES rack_definitions(rack_defs_id);

