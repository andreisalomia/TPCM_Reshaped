export const jwtUtils = {
    parseToken: (token) => {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload;
        } catch (error) {
            console.error('Failed to parse token:', error);
            return null;
        }
    },

    isTokenExpired: (token) => {
        try {
            const payload = jwtUtils.parseToken(token);
            if (!payload || !payload.exp) {
                return true;
            }

            const expirationTime = payload.exp * 1000;
            const currentTime = Date.now();

            return currentTime >= expirationTime;
        } catch (error) {
            console.error('Error checking token expiration:', error);
            return true;
        }
    },

    getToken: () => {
        return localStorage.getItem('tpcm_token');
    },

    saveToken: (token) => {
        localStorage.setItem('tpcm_token', token);
    },

    removeToken: () => {
        localStorage.removeItem('tpcm_token');
    },

    getUserFromToken: () => {
        const token = jwtUtils.getToken();

        if (!token) {
            return null;
        }

        if (jwtUtils.isTokenExpired(token)) {
            jwtUtils.removeToken();
            return null;
        }

        const payload = jwtUtils.parseToken(token);
        if (!payload) {
            return null;
        }

        return {
            msisdn: payload.msisdn,
            email: payload.email,
            name: payload.name,
            role: payload.role,
            subscriberId: payload.sub,
            authenticated: true
        };
    },

    isAdmin: () => {
        const user = jwtUtils.getUserFromToken();
        return user?.role === 'ADMIN';
    },

    getTimeUntilExpiration: () => {
        const token = jwtUtils.getToken();
        if (!token) return 0;

        const payload = jwtUtils.parseToken(token);
        if (!payload || !payload.exp) return 0;

        const expirationTime = payload.exp * 1000;
        const currentTime = Date.now();
        const timeRemaining = expirationTime - currentTime;

        return Math.max(0, timeRemaining);
    }
};