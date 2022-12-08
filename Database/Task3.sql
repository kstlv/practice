SELECT
	Customers.Name
FROM
	Customers
WHERE
	Customers.row_id NOT IN (
		SELECT
			Customers.row_id
		FROM
			Customers
			JOIN Orders
			JOIN OrderItems ON Orders.row_id = OrderItems.order_id ON Customers.row_id = Orders.customer_id
		WHERE
			OrderItems.order_id NOT IN (
				SELECT
					OrderItems.order_id
				FROM
					OrderItems
				WHERE
					OrderItems.name = 'Кассовый аппарат'
			)
			AND YEAR(Orders.registered_at) = 2020
		GROUP BY
			Customers.row_id
	)