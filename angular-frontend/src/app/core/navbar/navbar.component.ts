import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AuthenticationService } from 'src/app/user/services/authentication.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  
  constructor(
    private authenticationService: AuthenticationService,
    private toastr: ToastrService,
    private router: Router
  ) { }

  ngOnInit(): void {
    
  }

  logout() {
    this.authenticationService.logout().subscribe(
      result => {
        localStorage.removeItem('user');
        this.toastr.success('Successful logout!');
        this.router.navigate(['users/login']).then(() => window.location.reload());
      },
      error => {
        this.toastr.error('An error occurred!');
        console.log(error);
      }
    )
  }
}
