insert into rack_definitions (rack_defs_id, RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)
values (1, 'L', 1000, 10, 30, 
SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(0,0,  20,0,  20,10,   10,10,   10,30,   0,30,  0,0)
	));
  
      insert into racks (racks_type, racks_geometry, racks_rotation)
  values (1, SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(30,30,  50,30,  50,40,   40,40,   40,60,   30,60,  30,30)
	), 3);

insert into rack_definitions (rack_defs_id, RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)
values (2, 'T', 1000, 10, 30, 
SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(0,0,  10,0,  10,10,   20,10,   20,20,   10,20,  10,30,   0,30,  0,0)
	));
  
    
      insert into racks (racks_type, racks_geometry, racks_rotation)
  values (2, SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(100,20,  110,20,  110,30,   120,30,   120,40,   110,40,  110,50,   100,50,  100,20)
	), 0);
  
insert into rack_definitions (rack_defs_id, RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)
values (3, 'II', 1000, 10, 30, 
SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(0,0,  10,0,  10,30,   0,30,   0,0)
	));
  
        insert into racks (racks_type, racks_geometry, racks_rotation)
  values (3, SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(50,90,  60,90,  60,120,   50,120,   50,90)
	), 0);
  
  insert into rack_definitions (rack_defs_id, RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)
values (4, 'III', 1000, 10, 30, 
SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(0,0,  10,0,  10,40,   0,40,   0,0)
	));
  
  insert into rack_definitions (RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)
values ('I', 1000, 10, 30, 
SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(0,0,  10,0,  10,20,   0,20,   0,0)
	));
  
  insert into rack_definitions (RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)
values ('O', 1000, 10, 30, 
SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 4), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(10,0,  20,10,  10,20)
	));
  
    insert into rack_definitions (RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)
values ('OO', 1000, 10, 30, 
SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 4), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(15,0,  30,15,  15,30)
	));
  
    insert into rack_definitions (RACK_DEFS_NAME, rack_defs_capacity, rack_defs_size_x, rack_defs_size_y, rack_defs_shape)
values ('D', 1000, 10, 30, 
SDO_GEOMETRY(2003, NULL, NULL, -- 2D polygon
		SDO_ELEM_INFO_ARRAY(1, 1003, 1), -- exterior polygon (counterclockwise)
		SDO_ORDINATE_ARRAY(0,0,  20,0,  20,20, 0,20,   0,0)
	));
  