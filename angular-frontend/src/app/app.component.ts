import { Component } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  
  title = 'Social Network';
  logedIn = false;

  constructor(
    private router: Router
  ) { }

  checkLogin() {
    const item = localStorage.getItem('user');
    
		if (!item && !(this.router.url == '/users/register')) {
			this.router.navigate(['/users/login']);
			return;
		} else if (!item && this.router.url == '/users/register') {
      this.router.navigate(['/users/register']);
      return;
    }

    this.logedIn = true;
  }

}
