let currentUser = null;

async function checkAuthState() {
    try {
        const response = await fetch('/api/users/me');
        if (response.ok) {
            currentUser = await response.json();
            return true;
        }
        currentUser = null;
        return false;
    } catch (error) {
        currentUser = null;
        return false;
    }
}

async function logout() {
    await fetch('/logout', { method: 'POST' });
    currentUser = null;
}

function isLoggedIn() {
    return currentUser !== null;
}

function getCurrentUser() {
    return currentUser;
}

export default {
    checkAuthState,
    logout,
    isLoggedIn,
    getCurrentUser,
};