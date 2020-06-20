create table posts (
    id serial primary key not null,
    title varchar(150) not null,
    body text not null default '',
    keywords varchar(50)[]
);

create table comments (
    id serial primary key not null,
    post_id integer not null references posts(id) on delete cascade,
    text text not null
)
