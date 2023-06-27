export class User {
    _id: number;
    username: string;
    password: string;
    email: string;
    lastLogin: string;
    firstName: string;
    lastName: string;
    displayName: string;
    description: string;

    constructor(obj: {
        _id?: number,
        username?: string,
        password?: string,
        email?: string,
        lastLogin?: string,
        firstName?: string,
        lastName?: string,
        displayName?: string,
        description?: string
    } = {}) {
        this._id = obj._id || null as unknown as number;
        this.username = obj.username || null as unknown as string;
        this.password = obj.password || null as unknown as string;
        this.email = obj.email || null as unknown as string;
        this.lastLogin = obj.lastLogin || null as unknown as string;
        this.firstName = obj.firstName || null as unknown as string;
        this.lastName = obj.lastName || null as unknown as string;
        this.displayName = obj.displayName || null as unknown as string;
        this.description = obj.description || null as unknown as string;
    }

    get id() {
        return this._id;
    }
}
