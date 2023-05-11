import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Social Network';
}

if (document.URL === "http://localhost:4200/") {
  document.title = "Home Page";
} else {
  document.title = "Groups";
}
