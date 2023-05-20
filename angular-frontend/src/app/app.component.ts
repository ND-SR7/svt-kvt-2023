import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title: string = 'Social Network';
  public logedIn: boolean;

  constructor(
    private router: Router
  ) { this.logedIn = false; }
  
  checkLogin() {
    const item = localStorage.getItem('user');

    if(!item) {
      this.router.navigate(['/users/login']);
      this.logedIn = false;
      return;
    }
    this.logedIn = true;
  }
}
