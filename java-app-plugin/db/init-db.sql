CREATE TABLE customer_sales (
    id INT PRIMARY KEY NOT NULL,
    customer_name TEXT NOT NULL,
    item_name TEXT NOT NULL,
    item_count INT NOT NULL,
    amount DECIMAL(5,2) NOT NULL,
    date_of_sale DATE NOT NULL,
    comments TEXT
) ;

INSERT INTO customer_sales VALUES (
    'S-12345',
    'Acme Inc.',
    'Rocket-powered roller-skates', 1,
    1000.00,
    '2002-12-12',
    'Customer is reselling on to a third party looking for equipment to help catch local widlife.'
), (
    'S-12346',
    'Igloos R Us',
    'Ice-maker', 10,
    5000.00,
    '2002-12-19',
    NULL
), (
    'S-12347',
    'Acme Inc.',
    'Giant spring-board', 1,
    700.00,
    '2002-12-13' ,
    NULL
) ;

-- --------------------------------------------------------------------

CREATE TABLE simple_rss_items (
    link TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL
) ;

INSERT INTO simple_rss_items VALUES (
    'https://awasu.com',
    'Awasu',
    'This is a demonstration RSS item that links to Awasu.'
), (
    'https://google.com',
    'Google',
    'This is a demonstration RSS item that links to Google.'
), (
    'https://codeproject.com',
    'Code Project',
    'This is a demonstration RSS item that links to Code Project'
) ;
