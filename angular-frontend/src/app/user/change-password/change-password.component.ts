import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../services/authentication.service';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ChangePassword } from '../model/changePassword.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authenticationService: AuthenticationService,
    private toastr: ToastrService,
    private router: Router
  ) {
    this.form = this.fb.group({
      oldPassword: [null, Validators.required],
      newPassword1: [null, Validators.required],
      newPassword2: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    
  }

  submit() {
    if (this.form.value.newPassword1 != this.form.value.newPassword2) {
      this.toastr.warning('Inputs for new password do not match!');
      return;
    }

    const auth: ChangePassword = new ChangePassword();
    auth.oldPassword = this.form.value.oldPassword;
		auth.newPassword = this.form.value.newPassword1;

    this.authenticationService.changePassword(auth).subscribe(
      result => {
        this.toastr.success('Successfully changed password!');
        localStorage.removeItem('user');
        this.router.navigate(['/users/login']).then(() => window.location.reload());
      },
      error => {
        this.toastr.error('An error occurred!');
        console.log(error);        
      }
    )
  }
}
