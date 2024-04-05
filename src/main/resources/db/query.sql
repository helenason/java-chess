CREATE TABLE game (
    game_id INT NOT NULL auto_increment,
    turn VARCHAR(10) NOT NULL,
    PRIMARY KEY (game_id)
);

CREATE TABLE board (
	board_id INT NOT NULL auto_increment,
    file_column INT NOT NULL,
    rank_row INT NOT NULL,
    piece_type VARCHAR(10) NOT NULL,
    piece_color VARCHAR(10) NOT NULL,
    game_id INT NOT NULL,
	PRIMARY KEY (board_id),
	FOREIGN KEY (game_id) REFERENCES game(game_id)
);
