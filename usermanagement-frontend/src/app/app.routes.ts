import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { ProfileComponent } from './profile/profile.component';
import { UpdateuserComponent } from './updateuser/updateuser.component';
import { usersGuard,adminGuard } from './users.guard';

export const routes: Routes = [

    {path: 'login', component: LoginComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'profile', component: ProfileComponent},
    {path: 'update/:id', component: UpdateuserComponent},
    // {path: 'users', component: UserslistComponent, canActivate:[adminGuard]},
    {path: '**', component: LoginComponent},
    {path: '', redirectTo: '/login', pathMatch: 'full'},
    
];
