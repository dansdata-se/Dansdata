SELECT
    "key" AS "id",
    "name"
    FROM
        dansdata.bands
    WHERE
        "key" = $1;
