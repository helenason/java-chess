CREATE TABLE game (
    turn VARCHAR(10) NOT NULL
);

CREATE TABLE board (
	board_id INT NOT NULL auto_increment,
    file_column INT NOT NULL,
    rank_row INT NOT NULL,
    piece_type VARCHAR(10) NOT NULL,
    piece_color VARCHAR(10) NOT NULL,
	PRIMARY KEY (board_id)
);
