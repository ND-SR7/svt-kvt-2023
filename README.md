Projekat (SVT, KVT) 2023

**Društvena mreža**

Potrebno je implementirati aplikaciju društvene mreže. Kao inspiraciju možete koristiti društvene mreže koje poznajete poput *Facebook*-a, *Instagram*-a, *Twitter*-a i sl.

---

Korisnik aplikacije ima na raspolaganju sledeće funkcionalnosti:

[K1] Registracija korisnika. Administrator sistema je predefinisan korisnik u sistemu. [K2] Prijava i odjava sa sistema. Na formi za prijavljivanje na sistem postoji link za prelaz na registraciju korisnika. Kada se korisnik uspešno prijavi na aplikaciju omogućiti korisniku da se odjavi. Bez prijave na sistem nije moguće pristupiti ostatku aplikacije.

[K3] Rukovanje[^1] objavama. Objava može i ne mora da sadrži slike. Ukoliko ih sadrži, može da sadrži jednu ili više njih. Obavezno polje objave su jeste sadržaj tj. tekst te objave.

[K4] Ažuriranje komentara. Moguće je odgovoriti (eng. *reply*) na komentar. Moguć je proizvoljan broj odgovora na komentar (*reply* na *reply* na *reply*…).

[K5] Korisnicima sistema je omogućeno reagovanje na objave i komentare (*like*, *dislike, heart*).

[K6] Sortiranje komentara. Moguće je sortirati komentare prema broju lajkova (eng. *like*), broju dislajkova (eng. *dislike*), broju srca (eng. *heart*) - opadajuće i rastuće, prema datumu objavljivanja (eng. *new*) od najskorijeg ka najstarijem ili od najstarijeg ka najskorijem (eng. *old*).

[K7] Sortiranje objava. Objave je moguće sortirati po datumu objavljivanja (eng. *new*) rastuće i opadajuće.

[K8] Rukovanje grupama. Bilo koji registrovan korisnik može da kreira grupu i automatski postaje administrator date grupe. Jedino administrator sistema ima pravo da suspenduje (logički obriše) grupu.

Svaki korisnik može da uđe u grupu i prati objave unutar te grupe i kreira objave unutar te grupe. Da bi korisnik ušao u grupu potrebno je da napravi zahtev za ulazak u grupu.

[K9] Prijavljivanje neprikladnog komentara, objave ili korisnika. Svaki korisnik ima pravo da prijavi sadržaj ili korisnika kao neprikladan. Prijavljen sadržaj se pregleda od strane administratora sistema. Prijavljeni sadržaj (komentar, objava) i korisnik mogu biti suspendovani od strane administratora sistema (logički obrisani ili u slučaju korisnik blokirani). Blokiran korisnik ne može da se prijavi na sistem, dok se suspendovan sadržaj dalje ne prikazuje na sistemu. Administrator sistema takođe ima pregled svih blokiranih korisnika koje u svakom momentu može odblokirati.

[K10] Pregled početne stranice. Registrovani korisnici na početnoj stranici vide nasumične objave koje su javne (svoje i objave svojih prijatelja) ili nasumične objave iz nasumičnih grupa kojima pripada. Pored objave nalazi se link koji vodi ka datoj grupi ukoliko je objava iz grupe. Pod javnom objavom se misli na objavu koju korisnik kreira van grupe i ona je javna tj. vidljiva na nivou cele aplikacije.

[K11] Promena lozinke. Pri promeni lozinke prvo se unosi trenutna lozinka i dva puta se unosi nova lozinka.

[K12] Promena dodatnih podataka na profilu - moguće je podesiti ime koje se prikazuje umesto korisničkog imena i postaviti opis svog profila, kao i sliku. Na profilu se takođe vidi i spisak svih grupa kojima korisnik pripada.

[K13] Pretraga korisnika na sistemu s ciljem pronalaska novih prijatelja. Pretraga se vrši na serverskoj strani na osnovu imena i prezimena korisnika. Korisnik sistema može da pošalje zahtev za prijateljstvo svakom korisniku sistema. Prijateljstvo se uspostavlja onda kada druga strana odobri zahtev. Svaki zahtev se može i odbiti.

---

Administrator grupe ima na raspolaganju sve funkcionalnosti koje ima i registrovan korisnik, kao i:

[M1] Blokiranje i odblokiranje korisnika te grupe. Blokiranje (eng. *ban*) sprečava korisnika da ima pristup grupi dok god je u stanju blokiran. To znači da ne može da objavljuje u grupi i na njegovoj početnoj stranici se neće prikazivati objave iz te grupe.

[M2] (nastavak na K8) Administrator grupe ima pregled svih pristiglih zahteva za pridruženje grupi. Tek nakon što administrator grupe odobri zahtev korisnik postaje deo grupe. Administrator grupe takođe, može svaki pristigli zahtev i da odbije.

---

Administrator sitema ima na raspolaganju sve funkcionalnosti koje ima registrovan korisnik, ali i:

[A1] Uklanjanje administratora grupa. Uklonjen administrator grupe postaje običan korisnik.

[A2] Suspendovanje grupe. Prilikom suspendovanja grupe administrator sistema navodi tekstualan opis za razlog suspendovanja. Aplikacija automatski uklanja sve administratore grupe sa suspendovane grupe.

---

Za implementaciju aplikacije iskoristiti sledeće softverske pakete:

- Za Serverske veb tehnologije:
  - Spring framework
  - Apache Tomcat (ne mora biti posebno integrisan, može Spring Boot)
  - MySQL
- Za Klijentske veb aplikacije
    - Angular framework

Podatke kojima upravlja aplikacija organizovati uz oslonac na SUBP[^2].

---

**Nefunkcionalni zahtevi**

Podržati autentifikaciju korisnika upotrebom korisničkog imena i lozinke i autorizaciju korisnika upotrebom mehanizma tokena.

Beležiti poruke o važnim događajima koji su nastali prilikom izvršavanja aplikacije.

---

**Arhitektura aplikacije**

Aplikacija je raspoređena na tri uređaja: Veb pretraživač, Spring kontejner (u Tomcat serveru ili pokrenut pomoću Spring Boot) i SUBP. Dijagram rasporeda prikazan je na slici 1.


Back-end aplikaciju implementirati upotrebom Spring framework-a [1], dozvoljeno je koristiti i Spring Boot [2]. Front-end aplikacija mora postojati i komunicira sa back-end aplikacijom putem RESTful servisa. Kao SUBP koristiti MySQL Server [3] ili neki drugi relacioni SUBP. Za beleženje poruka koristiti log4j API [4]. Za izgradnju softvera koristiti Apache Maven [5] ili neki drugi alat, a dozvoljeno je i koristiti Spring Boot i na taj način konfigurisati i pokretati aplikaciju.

Veb aplikacija je Angular aplikacija i ocenjuje se na *Klijentskim veb aplikacijama* (KVA). Ukoliko postoje studenti koji su položili *Softverske veb tehnologije* (SVT), a nisu položili KVA - *backend* deo rešenja može biti realizovan kroz *Firebase* [6] ili neki sličan servis.

---

**Model podataka**

Na [linku](https://drive.google.com/file/d/1SeS5CEozVGobi3cES3UXqNUHxtNdaHdt/view?usp=sharing) prikazan je model podataka aplikacije. Entitet User predstavlja registrovanog korisnika aplikacije i namenjen je skladištenju podataka koji se koriste za autentifikaciju i autorizaciju. Neregistrovan korisnik ima jedino mogućnost registrovanja na aplikaciju. Korisnik takođe može biti administrator sistema ili administrator grupe. Administrator grupe održava određenu grupu, dok administrator sistema rukovodi aplikacijom i ima mogućnost uklanjanja grupa. Objave su opisane entitetom Post i one su tekstualne, ali mogu sadržati i slike. Entitet Comment predstavlja komentar u datoj aplikaciji. Entitet Reaction predstavlja reakciju na određene objave ili komentara. Ukoliko sadržaj krši pravila zajednice ili aplikacije, kreira se entitet Report koji se odnosi na objavu, komentar ili korisnika. Entitet Group predstavlja grupu koji sadrži objave i komentare i njoj pripadaju korisnici, a održava je administrator grupe.

---

**Reference**

1. Spring Framework, <https://spring.io/>
2. Spring Boot, <https://projects.spring.io/spring-boot/>
3. MySQL, <https://www.mysql.com/>
4. Log4j, <https://logging.apache.org/log4j/2.x/>
5. Maven, <https://maven.apache.org/>

---

[^1]: Pod rukovanjem se podrazumevaju aktivnosti vezane za dodavanje, promenu i uklanjanje odgovarajućih pojava entiteta. Većinu informacionih sistema karakteriše neograničen period čuvanja podataka te se aktivnost uklanjanja odgovarajućih pojava entiteta retko koristi.
[^2]: Skraćeno od Sistem za Upravljanje Bazama Podataka.
