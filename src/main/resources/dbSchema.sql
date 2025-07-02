create table if not exists users (
                                     id SERIAL PRIMARY KEY,
                                     email_address TEXT NOT NULL UNIQUE,
                                     username TEXT NOT NULL UNIQUE,
                                     password CHAR(256) NOT NULL
);

create table if not exists project (
                                       id SERIAL PRIMARY KEY,
                                       name TEXT NOT NULL,
                                       description TEXT,
                                       archived BOOLEAN,
                                       owner_id SERIAL REFERENCES users(id)
);

create table if not exists issues (
                                      id SERIAL PRIMARY KEY,
                                      title TEXT NOT NULL,
                                      description TEXT,
                                      status TEXT DEFAULT 'open',
                                      created_by SERIAL REFERENCES users(id),
                                      project_id SERIAL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table if not exists comments (
                                        id SERIAL PRIMARY KEY,
                                        author SERIAL REFERENCES users(id),
                                        text TEXT,
                                        issue_ID SERIAL REFERENCES issues(id)
);

create table if not exists issue_links (
                                           linker SERIAL REFERENCES issues(id),
                                           linked SERIAL REFERENCES issues(id)
);