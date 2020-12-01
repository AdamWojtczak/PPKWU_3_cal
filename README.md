# PPKWU_3_cal
### Weeia calendar scraper
API służące do pobierania plików .ics generowanych na podstawwie kalendarza weeia dostepnego na stronie http://www.weeia.p.lodz.pl/

### Przykładowe wywołania
Dla parametrów year = 2020 i month = 03:
```
http://localhost:8080/cal/?year=2020&month=03
```
pobiera plik vCalendar dla miesiąca i roku wskazanego w parametrach: month i year.

### Przykładowe plik dla października

```
BEGIN:VCALENDAR
PRODID:-//AdamWojtczak//iCal4j 1.0/PL
VERSION:2.0
CALSCALE:GREGORIAN
BEGIN:VEVENT
DTSTAMP:20201124T144540Z
DTSTART;VALUE=DATE:20201001
SUMMARY:
UID:adres.synchronizacji@example.com
URL:
END:VEVENT
BEGIN:VEVENT
DTSTAMP:20201124T144540Z
DTSTART;VALUE=DATE:20201008
SUMMARY:Wielka Integracja WIP
UID:adres.synchronizacji@example.com
URL:
END:VEVENT
BEGIN:VEVENT
DTSTAMP:20201124T144540Z
DTSTART;VALUE=DATE:20201009
SUMMARY:Wielka Integracja WIP
UID:adres.synchronizacji@example.com
URL:
END:VEVENT
BEGIN:VEVENT
DTSTAMP:20201124T144540Z
DTSTART;VALUE=DATE:20201010
SUMMARY:Wielka Integracja WIP
UID:adres.synchronizacji@example.com
URL:
END:VEVENT
END:VCALENDAR
```


### Biblioteki zewnętrzne
Przy tworzeniu pliku w formacie .ics korzystam z biblioteki ical4j (http://ical4j.github.io/).
Do skrapowania kalendarza z weeia.p.lodz.pl korzystam z biblioteki JSoup (https://jsoup.org/).

