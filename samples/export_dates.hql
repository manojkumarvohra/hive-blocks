select concat(date_format(current_date(), 'yyyy-MM-dd'), ' 00:00:00') from_date, concat(date_format(current_date(), 'yyyy-MM-dd'), ' 23:59:59') to_date from default.dual;