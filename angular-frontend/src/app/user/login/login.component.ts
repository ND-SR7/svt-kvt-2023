import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthenticationService } from '../services/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authenticationService: AuthenticationService,
    private router: Router
  ) {
      this.form = this.fb.group({
			  username: [null, Validators.required],
			  password: [null, Validators.required]
		});
  }

  ngOnInit(): void {
    
  }

  submit() {
    const auth: any = {};
		auth.username = this.form.value.username;
		auth.password = this.form.value.password;

		this.authenticationService.login(auth).subscribe(
			result => {
				window.alert('Successful login!');
				localStorage.setItem('user', JSON.stringify(result));
				this.router.navigate(['groups']);
			},
			error => {
				console.log(error);
			}
		);
  }

}
